package com.netease.easy.rpc.core.netty.http.client;

import com.netease.easy.rpc.core.bean.EasyRpcResponse;
import com.netease.easy.rpc.core.bean.EasyRpcResponseFuture;
import com.netease.easy.rpc.core.netty.AbstractEasyRpcClientHandler;
import com.netease.easy.rpc.core.netty.tcp.manage.registries.EasyRpcResponseFutureRegistry;
import com.netease.easy.rpc.core.util.HessianSerializerUtil;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;

/**
 * @author zhuhai
 * @date 2023/12/28
 */
public class EasyRpcHttpEasyRpcClientHandler extends AbstractEasyRpcClientHandler<FullHttpResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyRpcHttpEasyRpcClientHandler.class);

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
}
