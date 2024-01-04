package com.github.easy.rpc.core.netty.http.server;


import com.github.easy.rpc.core.config.EasyRpcProperties;
import com.github.easy.rpc.core.enums.ChannelState;
import com.github.easy.rpc.core.exception.EasyRpcException;
import com.github.easy.rpc.core.netty.base.AbstractEasyRpcServer;
import com.github.easy.rpc.core.netty.tcp.manage.registries.ServiceProviderInstanceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuhai
 * @date 2023/12/28
 */
public class EasyRpcHttpServer extends AbstractEasyRpcServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyRpcHttpServer.class);

    public EasyRpcHttpServer(ServiceProviderInstanceRegistry registry) {
        super(registry, new EasyRpcProperties());
    }

    public EasyRpcHttpServer(ServiceProviderInstanceRegistry serviceProviderInstanceRegistry, EasyRpcProperties properties) {
        super(serviceProviderInstanceRegistry, properties);
    }

    @Override
    public void openSync() {
        initRequestExecutor();
        startServer();
    }

    @Override
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
        LOGGER.info("easy-rpc http server is starting, port:{}", properties.getServerPort());
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
                                .addLast(new HttpServerCodec())
                                .addLast(new HttpObjectAggregator(properties.getHttpMaxContentLength()))
                                .addLast(new EasyRpcHttpServerHandler(localMethodExecutor, serviceProviderInstanceRegistry));
                    }
                }).childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture channelFuture = serverBootstrap.bind(new InetSocketAddress(port)).sync();
        serverChannel = channelFuture.channel();
        state = ChannelState.ALIVE;
        channelFuture.channel().closeFuture().sync();
    }
}
