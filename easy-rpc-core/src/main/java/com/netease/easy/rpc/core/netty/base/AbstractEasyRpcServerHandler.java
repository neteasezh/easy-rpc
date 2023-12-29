package com.netease.easy.rpc.core.netty.base;

import com.netease.easy.rpc.core.bean.EasyRpcRequest;
import com.netease.easy.rpc.core.bean.EasyRpcResponse;
import com.netease.easy.rpc.core.exception.EasyRpcException;
import com.netease.easy.rpc.core.netty.tcp.manage.registries.ServiceProviderInstanceRegistry;
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
 * @date 2023/12/28
 */
public abstract class AbstractEasyRpcServerHandler<T> extends SimpleChannelInboundHandler<T> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractEasyRpcServerHandler.class);
    protected final ThreadPoolExecutor threadPoolExecutor;
    protected final ServiceProviderInstanceRegistry serviceProviderInstanceRegistry;

    protected AbstractEasyRpcServerHandler(ThreadPoolExecutor threadPoolExecutor, ServiceProviderInstanceRegistry serviceProviderInstanceRegistry) {
        this.threadPoolExecutor = threadPoolExecutor;
        this.serviceProviderInstanceRegistry = serviceProviderInstanceRegistry;
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

    /**
     * 执行调用
     * @param request
     * @return
     */
    protected EasyRpcResponse doInvoke(EasyRpcRequest request) {
        EasyRpcResponse response = new EasyRpcResponse();
        response.setRequestId(request.getRequestId());
        Object serviceProvider = serviceProviderInstanceRegistry.getServiceProvider(request.getClassName());
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

}
