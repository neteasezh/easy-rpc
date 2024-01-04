package com.github.easy.rpc.core.netty.tcp.server;

import com.github.easy.rpc.core.bean.EasyRpcRequest;
import com.github.easy.rpc.core.bean.EasyRpcResponse;
import com.github.easy.rpc.core.bean.HeartBeat;
import com.github.easy.rpc.core.netty.base.AbstractEasyRpcServerHandler;
import com.github.easy.rpc.core.netty.tcp.manage.registries.ServiceProviderInstanceRegistry;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zhuhai
 * @date 2023/12/20
 */
public class EasyRpcServerHandler extends AbstractEasyRpcServerHandler<EasyRpcRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyRpcServerHandler.class);
    public EasyRpcServerHandler(ThreadPoolExecutor threadPoolExecutor, ServiceProviderInstanceRegistry serviceProviderInstanceRegistry) {
        super(threadPoolExecutor, serviceProviderInstanceRegistry);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EasyRpcRequest request) throws Exception {
        if (HeartBeat.BEAT_MSG.equals(request.getRequestId())) {
            LOGGER.info("server is reading ping-pong...");
            return;
        }
        try {
            threadPoolExecutor.execute(() -> {
                EasyRpcResponse response = doInvoke(request);
                ctx.writeAndFlush(response);
            });
        } catch (Throwable e) {
            EasyRpcResponse response = new EasyRpcResponse();
            response.setRequestId(request.getRequestId());
            response.setError(e);
            ctx.writeAndFlush(response);
        }
    }
}
