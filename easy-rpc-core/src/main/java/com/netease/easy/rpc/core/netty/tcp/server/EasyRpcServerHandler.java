package com.netease.easy.rpc.core.netty.server;

import com.netease.easy.rpc.core.bean.EasyRpcRequest;
import com.netease.easy.rpc.core.bean.EasyRpcResponse;
import com.netease.easy.rpc.core.bean.HeartBeat;
import com.netease.easy.rpc.core.netty.manage.registries.ServiceProviderRegistry;
import com.netease.easy.rpc.core.exception.EasyRpcException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zhuhai
 * @date 2023/12/20
 */
public class EasyRpcServerHandler extends SimpleChannelInboundHandler<EasyRpcRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyRpcServerHandler.class);
    private final ThreadPoolExecutor threadPoolExecutor;

    private final ServiceProviderRegistry serviceProviderRegistry;

    public EasyRpcServerHandler(ThreadPoolExecutor threadPoolExecutor, ServiceProviderRegistry serviceProviderRegistry) {
        this.threadPoolExecutor = threadPoolExecutor;
        this.serviceProviderRegistry = serviceProviderRegistry;
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

    /**
     * 执行调用
     * @param request
     * @return
     */
    private EasyRpcResponse doInvoke(EasyRpcRequest request) {
        EasyRpcResponse response = new EasyRpcResponse();
        response.setRequestId(request.getRequestId());
        Object serviceProvider = serviceProviderRegistry.getServiceProvider(request.getClassName());
        if (Objects.isNull(serviceProvider)) {
            response.setError(new EasyRpcException("service provider not found"));
            return response;
        }
        try {
            Class<?> clazz = serviceProvider.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();
            Method method = clazz.getMethod(methodName, parameterTypes);
            Object result = method.invoke(serviceProvider, parameters);
            response.setResult(result);
        } catch (Throwable e) {
            response.setError(e);
        }
        return response;
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;
        if (event.state() == IdleState.READER_IDLE) {
            ctx.channel().close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("easy rpc server caught exception...", cause);
        ctx.close();
    }
}
