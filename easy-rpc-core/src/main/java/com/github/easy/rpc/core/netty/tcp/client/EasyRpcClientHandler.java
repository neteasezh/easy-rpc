package com.github.easy.rpc.core.netty.tcp.client;

import com.github.easy.rpc.core.bean.EasyRpcResponse;
import com.github.easy.rpc.core.bean.EasyRpcResponseFuture;
import com.github.easy.rpc.core.netty.base.AbstractEasyRpcClientHandler;
import com.github.easy.rpc.core.netty.tcp.manage.registries.EasyRpcResponseFutureRegistry;
import io.netty.channel.ChannelHandlerContext;
import java.util.Objects;

/**
 * @author zhuhai
 * @date 2023/12/20
 */
public class EasyRpcClientHandler extends AbstractEasyRpcClientHandler<EasyRpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EasyRpcResponse resp) throws Exception {
        String requestId = resp.getRequestId();
        EasyRpcResponseFuture responseFuture = EasyRpcResponseFutureRegistry.getResponseFuture(requestId);
        if (Objects.nonNull(responseFuture)) {
            responseFuture.setResponse(resp);
        }
    }
}
