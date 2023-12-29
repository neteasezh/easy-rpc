package com.netease.easy.rpc.core.netty.server;

import com.netease.easy.rpc.core.bean.EasyRpcRequest;
import com.netease.easy.rpc.core.bean.EasyRpcResponse;
import com.netease.easy.rpc.core.config.EasyRpcProperties;
import com.netease.easy.rpc.core.enums.ChannelState;
import com.netease.easy.rpc.core.exception.EasyRpcException;
import com.netease.easy.rpc.core.netty.codec.EasyRpcDecoder;
import com.netease.easy.rpc.core.netty.codec.EasyRpcEncoder;
import com.netease.easy.rpc.core.netty.manage.registries.ServiceProviderRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author zhuhai
 * @date 2023/12/20
 * 服务端
 */
public class EasyRpcServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyRpcServer.class);
    private volatile ChannelState state = ChannelState.UN_INIT;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private EasyRpcProperties properties;
    private ThreadPoolExecutor requestExecutor;
    private ThreadPoolExecutor localMethodExecutor;
    private final ServiceProviderRegistry serviceProviderRegistry;

    public EasyRpcServer(ServiceProviderRegistry registry) {
        this.serviceProviderRegistry = registry;
        this.properties = new EasyRpcProperties();
    }


    public EasyRpcServer(ServiceProviderRegistry registry, EasyRpcProperties properties) {
        this.serviceProviderRegistry = registry;
        this.properties = properties;
    }

    /**
     * 初始化requestExecutor
     */
    private void initRequestExecutor() {
        if (Objects.isNull(requestExecutor)) {
            synchronized (EasyRpcServer.class) {
                if (Objects.isNull(requestExecutor)) {
                    requestExecutor
                            = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new DefaultThreadFactory("easy-rpc-server-handler", true));
                }
            }
        }
    }

    /**
     * 同步启动
     */
    public void openSync() {
        startServer();
    }

    /**
     * 异步启动
     */
    public void openAsync() {
        initRequestExecutor();
        // 异步启动
        CompletableFuture.runAsync(this::startServer, requestExecutor);
    }

    private void startServer() {
        if (state.isAliveState()) {
            LOGGER.warn("server is already open");
            return;
        }
        initLocalMethodExecutor();
        initEventLoopGroup();
        LOGGER.info("easy-rpc server is starting, port:{}", properties.getServerPort());
        try {
            doStartServer(properties.getServerPort());
        } catch (Exception e) {
            LOGGER.error("easy-rpc server start error", e);
            throw new EasyRpcException(e);
        } finally {
            close();
        }
    }

    /**
     * 启动server
     *
     * @param port
     * @throws InterruptedException
     */
    private void doStartServer(int port) throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(0, properties.getHeartbeatInterval() * 3, 0, TimeUnit.SECONDS))
                                .addLast(new EasyRpcDecoder(EasyRpcRequest.class))
                                .addLast(new EasyRpcEncoder(EasyRpcResponse.class))
                                .addLast(new EasyRpcServerHandler(localMethodExecutor, serviceProviderRegistry));
                    }
                }).childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);
        ChannelFuture channelFuture = serverBootstrap.bind(new InetSocketAddress(port)).sync();
        serverChannel = channelFuture.channel();
        state = ChannelState.ALIVE;
        channelFuture.channel().closeFuture().sync();
    }

    /**
     * 双重检验锁单例模式获取bossGroup/workerGroup
     */
    public void initEventLoopGroup() {
        if (Objects.isNull(bossGroup) || Objects.isNull(workerGroup)) {
            synchronized (EasyRpcServer.class) {
                if (Objects.isNull(bossGroup) || Objects.isNull(workerGroup)) {
                    bossGroup = new NioEventLoopGroup(properties.getServerBossWorkerThreads());
                    workerGroup = new NioEventLoopGroup(properties.getServerBossWorkerThreads());
                }
            }
        }
    }

    /**
     * 双重检验锁单例模式获取localMethodExecutor
     */
    public void initLocalMethodExecutor() {
        if (Objects.isNull(localMethodExecutor)) {
            synchronized (EasyRpcServer.class) {
                if (Objects.isNull(localMethodExecutor)) {
                    localMethodExecutor = new ThreadPoolExecutor(properties.getServerMessageProcessorCoreSize(), properties.getServerMessageProcessorCoreSize(), 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10000), new DefaultThreadFactory("easy-rpc-server", true), new ThreadPoolExecutor.AbortPolicy());
                    localMethodExecutor.prestartAllCoreThreads();
                }
            }
        }
    }

    public void close() {
        if (state.isCloseState()) {
            return;
        }
        LOGGER.info("easy-rpc server is closing");
        try {
            stop();
            if (state.isUnInitState()) {
                return;
            }
            state = ChannelState.CLOSE;
            LOGGER.info("easy-rpc server is closed");
        } catch (Exception e) {
            LOGGER.error("easy-rpc server close error", e);
        }
    }

    /**
     * 关闭server
     */
    public void stop() {
        if (Objects.nonNull(serverChannel)) {
            serverChannel.close();
        }
        if (Objects.nonNull(bossGroup)) {
            bossGroup.shutdownGracefully();
        }
        if (Objects.nonNull(workerGroup)) {
            workerGroup.shutdownGracefully();
        }
        if (Objects.nonNull(localMethodExecutor)) {
            localMethodExecutor.shutdown();
        }
        if (Objects.nonNull(requestExecutor)) {
            requestExecutor.shutdown();
        }
    }

    public Channel getServerChannel() {
        return serverChannel;
    }

    public void setServerChannel(Channel serverChannel) {
        this.serverChannel = serverChannel;
    }

    public ThreadPoolExecutor getLocalMethodExecutor() {
        return localMethodExecutor;
    }

    public void setLocalMethodExecutor(ThreadPoolExecutor localMethodExecutor) {
        this.localMethodExecutor = localMethodExecutor;
    }

    public EasyRpcProperties getProperties() {
        return properties;
    }

    public void setProperties(EasyRpcProperties properties) {
        this.properties = properties;
    }

    public ChannelState getState() {
        return state;
    }

    public void setState(ChannelState state) {
        this.state = state;
    }

    public ThreadPoolExecutor getRequestExecutor() {
        return requestExecutor;
    }

    public void setRequestExecutor(ThreadPoolExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
    }
}
