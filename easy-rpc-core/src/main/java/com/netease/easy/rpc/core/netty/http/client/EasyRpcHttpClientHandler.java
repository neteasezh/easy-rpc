package com.netease.easy.rpc.core.netty.http.client;

import com.netease.easy.rpc.core.bean.EasyRpcResponse;
import com.netease.easy.rpc.core.bean.EasyRpcResponseFuture;
import com.netease.easy.rpc.core.bean.HeartBeat;
import com.netease.easy.rpc.core.netty.base.AbstractEasyRpcClientHandler;
import com.netease.easy.rpc.core.netty.tcp.manage.registries.EasyRpcResponseFutureRegistry;
import com.netease.easy.rpc.core.util.HessianSerializerUtil;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;

/**
 * @author zhuhai
 * @date 2023/12/28
 */
public class EasyRpcHttpClientHandler extends AbstractEasyRpcClientHandler<FullHttpResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyRpcHttpClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse fullHttpResponse) throws Exception {
        byte[] bytes = ByteBufUtil.getBytes(fullHttpResponse.content());
        EasyRpcResponse response = (EasyRpcResponse) HessianSerializerUtil.deserialize(bytes, EasyRpcResponse.class);
        if (fullHttpResponse.status() != HttpResponseStatus.OK) {
            LOGGER.error("easy http client caught exception, status: {}, message: {}", fullHttpResponse.status(), response);
        }
        String requestId = response.getRequestId();
        EasyRpcResponseFuture responseFuture = EasyRpcResponseFutureRegistry.getResponseFuture(requestId);
        if (Objects.nonNull(responseFuture)) {
            responseFuture.setResponse(response);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;
        if (event.state() == IdleState.WRITER_IDLE) {
            byte[] bytes = HessianSerializerUtil.serialize(HeartBeat.request);
            DefaultFullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "", Unpooled.wrappedBuffer(bytes));
            fullHttpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, fullHttpRequest.content().readableBytes());
            ctx.channel().writeAndFlush(fullHttpRequest).sync();
        } else {
            super.userEventTriggered(ctx, evt);
        }

    }
}
