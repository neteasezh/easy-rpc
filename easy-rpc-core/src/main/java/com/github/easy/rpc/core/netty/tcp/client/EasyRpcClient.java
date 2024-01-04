package com.github.easy.rpc.core.netty.tcp.client;

import com.github.easy.rpc.core.bean.EasyRpcRequest;
import com.github.easy.rpc.core.bean.EasyRpcResponse;
import com.github.easy.rpc.core.bean.EasyRpcResponseFuture;
import com.github.easy.rpc.core.config.EasyRpcProperties;
import com.github.easy.rpc.core.netty.base.AbstractEasyRpcClient;
import com.github.easy.rpc.core.netty.tcp.manage.registries.EasyRpcResponseFutureRegistry;
import com.github.easy.rpc.core.exception.EasyRpcException;
import com.github.easy.rpc.core.netty.tcp.codec.EasyRpcDecoder;
import com.github.easy.rpc.core.netty.tcp.codec.EasyRpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuhai
 * @date 2023/12/19
 */
public class EasyRpcClient extends AbstractEasyRpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyRpcClient.class);

    public EasyRpcClient() {
        super(new EasyRpcProperties());
    }

    public EasyRpcClient(EasyRpcProperties properties) {
        super(properties);
    }


    /**
     * 初始化连接
     *
     * @param host
     * @param port
     * @throws Exception
     */
    @Override
    public void open(String host, int port) {
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
     * @param request
     * @param timeout

     * @return
     */
    @Override
    public EasyRpcResponse sendRequest(EasyRpcRequest request, Long timeout) {
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
}
