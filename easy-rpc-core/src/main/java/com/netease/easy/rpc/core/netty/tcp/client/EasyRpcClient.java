package com.netease.easy.rpc.core.netty.client;

import com.netease.easy.rpc.core.bean.EasyRpcRequest;
import com.netease.easy.rpc.core.bean.EasyRpcResponse;
import com.netease.easy.rpc.core.bean.EasyRpcResponseFuture;
import com.netease.easy.rpc.core.config.EasyRpcProperties;
import com.netease.easy.rpc.core.netty.manage.registries.EasyRpcResponseFutureRegistry;
import com.netease.easy.rpc.core.exception.EasyRpcException;
import com.netease.easy.rpc.core.netty.codec.EasyRpcDecoder;
import com.netease.easy.rpc.core.netty.codec.EasyRpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuhai
 * @date 2023/12/19
 */
public class EasyRpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyRpcClient.class);
    private NioEventLoopGroup eventLoopGroup;
    private Channel channel;
    private EasyRpcProperties properties;

    public EasyRpcClient() {
        this.properties = new EasyRpcProperties();
    }

    public EasyRpcClient(EasyRpcProperties properties) {
        this.properties = properties;
    }

    /**
     * 初始化eventLoopGroup
     */
    private void initEventLoopGroup() {
        if (eventLoopGroup == null) {
            synchronized (EasyRpcClient.class) {
                if (eventLoopGroup == null) {
                    eventLoopGroup = new NioEventLoopGroup();
                }
            }
        }
    }


    /**
     * 初始化连接
     *
     * @param host
     * @param port
     * @throws Exception
     */
    public void open(String host, int port) throws Exception {
        initEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new IdleStateHandler(0, properties.getHeartbeatInterval(), 0, TimeUnit.SECONDS))
                                    .addLast(new EasyRpcEncoder(EasyRpcRequest.class))
                                    .addLast(new EasyRpcDecoder(EasyRpcResponse.class))
                                    .addLast(new EasyRpcClientHandler());
                        }
                    }).option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeout());
            this.channel = bootstrap.connect(host, port).sync().channel();
            if (!isActive()) {
                close();
                return;
            }
            LOGGER.info("connect to server success, host:{}, port:{}", host, port);
        } catch (Throwable e) {
            close();
            LOGGER.error("connect to server error", e);
            throw new EasyRpcException("connect to server error");
        }
    }

    /**
     * 发送请求
     *
     * @param request
     * @param timeout
     * @return
     */
    public EasyRpcResponse sendRequest(EasyRpcRequest request, long timeout) {
        String requestId = request.getRequestId();
        EasyRpcResponseFuture responseFuture = new EasyRpcResponseFuture(request);
        EasyRpcResponseFutureRegistry.addResponseFuture(requestId, responseFuture);
        try {
            if (!isActive()) {
                throw new EasyRpcException("rpc client is not active");
            }
            this.channel.writeAndFlush(request).sync();
            return responseFuture.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            LOGGER.error("send request error", e);
            throw new EasyRpcException("send request error");
        } finally {
            EasyRpcResponseFutureRegistry.removeResponseFuture(requestId);
        }
    }


    /**
     * 判断连接是否有效
     *
     * @return
     */
    public boolean isActive() {
        return Objects.nonNull(channel) && channel.isActive();
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (Objects.nonNull(channel) && channel.isActive()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("ready to close channel:{}", channel.id());
            }
            channel.close();
        }
        if (Objects.nonNull(eventLoopGroup)) {
            eventLoopGroup.shutdownGracefully();
        }
    }


    public Channel getChannel() {
        return channel;
    }

    public EasyRpcProperties getProperties() {
        return properties;
    }

    public void setProperties(EasyRpcProperties properties) {
        this.properties = properties;
    }
}
