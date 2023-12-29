package com.netease.easy.rpc.core.netty.tcp.client;

import com.netease.easy.rpc.core.bean.EasyRpcResponse;
import com.netease.easy.rpc.core.bean.EasyRpcResponseFuture;
import com.netease.easy.rpc.core.netty.AbstractEasyRpcClientHandler;
import com.netease.easy.rpc.core.netty.tcp.manage.registries.EasyRpcResponseFutureRegistry;
import io.netty.channel.ChannelHandlerContext;
import java.util.Objects;

/**
 * @author zhuhai
 * @date 2023/12/20
 */
public class EasyRpcEasyRpcClientHandler extends AbstractEasyRpcClientHandler<EasyRpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EasyRpcResponse resp) throws Exception {
        String requestId = resp.getRequestId();
        EasyRpcResponseFuture responseFuture = EasyRpcResponseFutureRegistry.getResponseFuture(requestId);
        if (Objects.nonNull(responseFuture)) {
            responseFuture.setResponse(resp);
        }
    }
}
