package com.netease.easy.rpc.core.netty.http.server;

import com.netease.easy.rpc.core.bean.EasyRpcRequest;
import com.netease.easy.rpc.core.bean.EasyRpcResponse;
import com.netease.easy.rpc.core.bean.HeartBeat;
import com.netease.easy.rpc.core.exception.EasyRpcException;
import com.netease.easy.rpc.core.netty.base.AbstractEasyRpcServerHandler;
import com.netease.easy.rpc.core.netty.tcp.manage.registries.ServiceProviderInstanceRegistry;
import com.netease.easy.rpc.core.util.HessianSerializerUtil;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zhuhai
 * @date 2023/12/28
 */
public class EasyRpcHttpServerHandler extends AbstractEasyRpcServerHandler<FullHttpRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyRpcHttpServerHandler.class);

    public EasyRpcHttpServerHandler(ThreadPoolExecutor threadPoolExecutor, ServiceProviderInstanceRegistry serviceProviderInstanceRegistry) {
        super(threadPoolExecutor, serviceProviderInstanceRegistry);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
        byte[] bytes = ByteBufUtil.getBytes(fullHttpRequest.content());
        if (bytes == null || bytes.length == 0) {
            throw new EasyRpcException("request body is empty");
        }
        EasyRpcRequest request = (EasyRpcRequest) HessianSerializerUtil.deserialize(bytes, EasyRpcRequest.class);
        if (HeartBeat.BEAT_MSG.equals(request.getRequestId())) {
            LOGGER.info("server is reading ping-pong...");
            return;
        }
        try {
            threadPoolExecutor.execute(() -> {
                EasyRpcResponse response = doInvoke(request);
                DefaultFullHttpResponse fullHttpResponse = buildDefaultFullHttpResponse(response);
                ctx.writeAndFlush(fullHttpResponse);
            });
        } catch (Throwable e) {
            EasyRpcResponse response = new EasyRpcResponse();
            response.setRequestId(request.getRequestId());
            response.setError(e);
            DefaultFullHttpResponse fullHttpResponse = buildDefaultFullHttpResponse(response);
            ctx.writeAndFlush(fullHttpResponse);
        }
    }

    /**
     * 构建返回
     * @param response
     * @return
     */
    private  DefaultFullHttpResponse buildDefaultFullHttpResponse(EasyRpcResponse response) {
        DefaultFullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(HessianSerializerUtil.serialize(response)));
        fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, fullHttpResponse.content().readableBytes());
        fullHttpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        return fullHttpResponse;
    }
}
