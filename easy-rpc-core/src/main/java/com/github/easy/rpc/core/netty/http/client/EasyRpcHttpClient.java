package com.github.easy.rpc.core.netty.http.client;

import com.github.easy.rpc.core.bean.EasyRpcRequest;
import com.github.easy.rpc.core.bean.EasyRpcResponse;
import com.github.easy.rpc.core.bean.EasyRpcResponseFuture;
import com.github.easy.rpc.core.config.EasyRpcProperties;
import com.github.easy.rpc.core.exception.EasyRpcException;
import com.github.easy.rpc.core.netty.base.AbstractEasyRpcClient;
import com.github.easy.rpc.core.netty.tcp.manage.registries.EasyRpcResponseFutureRegistry;
import com.github.easy.rpc.core.util.HessianSerializerUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuhai
 * @date 2023/12/28
 */
public class EasyRpcHttpClient extends AbstractEasyRpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyRpcHttpClient.class);
    private static final String DEFAULT_PATH = "/easy-rpc-http";

    public EasyRpcHttpClient(EasyRpcProperties properties) {
        super(properties);
    }

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
                                    .addLast(new HttpClientCodec())
                                    .addLast(new HttpObjectAggregator(properties.getHttpMaxContentLength()))
                                    .addLast(new EasyRpcHttpClientHandler());
                        }
                    }).option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeout());
            this.channel = bootstrap.connect(host, port).sync().channel();
            if (!isActive()) {
                close();
                return;
            }
            LOGGER.info("connect to http server success, host:{}, port:{}", host, port);
        } catch (Throwable e) {
            LOGGER.error("easy rpc http client connect error...", e);
            close();
            throw new EasyRpcException("easy rpc http client connect error...");
        }
    }

    @Override
    public EasyRpcResponse sendRequest(EasyRpcRequest request, Long timeout) {
        String requestId = request.getRequestId();
        EasyRpcResponseFuture responseFuture = new EasyRpcResponseFuture(request);
        EasyRpcResponseFutureRegistry.addResponseFuture(requestId, responseFuture);

        try {
            if (!isActive()) {
                throw new EasyRpcException("rpc client is not active");
            }
            byte[] bytes = HessianSerializerUtil.serialize(request);
            DefaultFullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, DEFAULT_PATH, Unpooled.wrappedBuffer(bytes));
            fullHttpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, fullHttpRequest.content().readableBytes());
            this.channel.writeAndFlush(fullHttpRequest).sync();
            return responseFuture.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            throw new EasyRpcException("send request error");
        } finally {
            EasyRpcResponseFutureRegistry.removeResponseFuture(requestId);
        }
    }
}
