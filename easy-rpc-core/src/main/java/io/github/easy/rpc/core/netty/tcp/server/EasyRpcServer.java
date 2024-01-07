package io.github.easy.rpc.core.netty.tcp.server;

import io.github.easy.rpc.core.bean.EasyRpcRequest;
import io.github.easy.rpc.core.bean.EasyRpcResponse;
import io.github.easy.rpc.core.config.EasyRpcProperties;
import io.github.easy.rpc.core.enums.ChannelState;
import io.github.easy.rpc.core.exception.EasyRpcException;
import io.github.easy.rpc.core.netty.base.AbstractEasyRpcServer;
import io.github.easy.rpc.core.netty.tcp.codec.EasyRpcDecoder;
import io.github.easy.rpc.core.netty.tcp.codec.EasyRpcEncoder;
import io.github.easy.rpc.core.netty.tcp.manage.registries.ServiceProviderInstanceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


/**
 * @author zhuhai
 * @date 2023/12/20
 * 服务端
 */
public class EasyRpcServer extends AbstractEasyRpcServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyRpcServer.class);

    public EasyRpcServer(ServiceProviderInstanceRegistry registry) {
        super(registry, new EasyRpcProperties());
    }

    public EasyRpcServer(ServiceProviderInstanceRegistry registry, EasyRpcProperties properties) {
        super(registry, properties);
    }

    /**
     * 同步启动
     */
    public void openSync() {
        initRequestExecutor();
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
                                .addLast(new EasyRpcServerHandler(localMethodExecutor, serviceProviderInstanceRegistry));
                    }
                }).childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);
        ChannelFuture channelFuture = serverBootstrap.bind(new InetSocketAddress(port)).sync();
        serverChannel = channelFuture.channel();
        state = ChannelState.ALIVE;
        channelFuture.channel().closeFuture().sync();
    }
}
