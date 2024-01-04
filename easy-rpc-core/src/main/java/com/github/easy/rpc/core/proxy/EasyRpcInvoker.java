package com.github.easy.rpc.core.proxy;

import com.github.easy.rpc.core.annotation.EasyRpcApi;
import com.github.easy.rpc.core.enums.ProtocolEnum;
import com.github.easy.rpc.core.util.EasyRpcConstants;
import com.github.easy.rpc.core.bean.EasyRpcRequest;
import com.github.easy.rpc.core.bean.EasyRpcResponse;
import com.github.easy.rpc.core.config.RegistryConfig;
import com.github.easy.rpc.core.enums.LoadBalanceEnum;
import com.github.easy.rpc.core.exception.EasyRpcException;
import com.github.easy.rpc.core.netty.tcp.pool.EasyRpcClientPool;
import com.github.easy.rpc.core.util.CollectionUtils;
import com.github.easy.rpc.core.util.LRUCache;
import com.github.easy.rpc.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhuhai
 * @date 2023/12/20
 */
public class EasyRpcInvoker {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyRpcInvoker.class);
    private static final Integer PROXY_CACHE_SIZE = 1000;

    /**
     * 代理对象缓存
     */
    private Map<Class<?>, Object> proxyCache = new LRUCache<>(PROXY_CACHE_SIZE);

    /**
     * 客户端连接池
     */
    private EasyRpcClientPool pool;

    /**
     * 注册中心配置
     */
    private List<RegistryConfig> registries;

    public EasyRpcInvoker() {
        this.pool = new EasyRpcClientPool();
    }

    public EasyRpcInvoker(EasyRpcClientPool pool, List<RegistryConfig> registries) {
        this.pool = pool;
        this.registries = registries;
    }

    /**
     * 获取代理对象
     *
     * @param serviceCls
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T getProxy(final Class<T> serviceCls) throws Exception {
        if (!serviceCls.isInterface()) {
            throw new EasyRpcException("serviceCls must be a interface");
        }
        Object o = proxyCache.get(serviceCls);
        if (Objects.isNull(o)) {
            synchronized (this) {
                o = proxyCache.get(serviceCls);
                if (Objects.isNull(o)) {
                    o = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{serviceCls}, new EasyRpcInvocationHandler());
                    proxyCache.put(serviceCls, o);
                }
            }
        }
        return (T) o;
    }

    public EasyRpcClientPool getPool() {
        return pool;
    }

    public void setPool(EasyRpcClientPool pool) {
        this.pool = pool;
    }

    public List<RegistryConfig> getRegistries() {
        return registries;
    }

    public void setRegistries(List<RegistryConfig> registries) {
        this.registries = registries;
    }

    /**
     * 代理对象调用处理器
     */
    class EasyRpcInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            EasyRpcApi annotation = method.getAnnotation(EasyRpcApi.class);
            if (Objects.isNull(annotation)) {
                throw new EasyRpcException("method of proxy object must be annotated by @EasyRpcApi");
            }

            // 请求配置
            String appName = annotation.appName();
            String host = annotation.host();
            int port = annotation.port();
            int timeout = annotation.timeout();
            LoadBalanceEnum lb = annotation.lb();
            String provider = annotation.provider();
            ProtocolEnum protocol = annotation.protocol();

            // 基础校验
            validate(appName, host, provider);

            // 路由选择
            if (StringUtils.isBlank(host)) {
                Map<String, List<RegistryConfig>> map = registries.stream().collect(Collectors.groupingBy(RegistryConfig::getAppName));
                List<RegistryConfig> registryConfigs = map.get(appName.toUpperCase());
                if (CollectionUtils.isEmpty(registryConfigs)) {
                    throw new EasyRpcException("no registry config for appName:" + appName);
                }
                RegistryConfig config = lb.getStrategy().select(appName, registryConfigs);
                host = config.getHost();
                port = config.getPort();
            }

            // 请求方法、参数
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();

            // 如果是Object的普通方法，直接调用
            Method[] methods = Object.class.getMethods();
            List<String> objectMethodNames = Arrays.stream(methods).map(Method::getName).collect(Collectors.toList());
            if (objectMethodNames.contains(methodName)) {
                return method.invoke(this, args);
            }

            // 构造请求
            EasyRpcRequest request = buildRequest(args, provider, methodName, parameterTypes);

            // 发送请求
            return doRequest(host, port, timeout, request, protocol);
        }

        private void validate(String appName, String host, String provider) {
            if (StringUtils.isAllEmpty(appName, host)) {
                throw new EasyRpcException("(appName/registries) or (host+port) must be set");
            }
            if (StringUtils.isBlank(provider)) {
                throw new EasyRpcException("service provider class name must be set");
            }
            if (StringUtils.isBlank(host) && CollectionUtils.isEmpty(registries)) {
                throw new EasyRpcException("appName/registries must be set");
            }
        }

        /**
         * 构建请求
         *
         * @param args
         * @param className
         * @param methodName
         * @param parameterTypes
         * @return
         */
        private EasyRpcRequest buildRequest(Object[] args, String className,
                                            String methodName, Class<?>[] parameterTypes) {
            String requestId = UUID.randomUUID().toString().replace(EasyRpcConstants.HORIZONTAL_BAR_SYMBOL, StringUtils.EMPTY);
            EasyRpcRequest request = EasyRpcRequest.EasyRpcRequestBuilder.builder()
                    .requestId(requestId)
                    .className(className)
                    .methodName(methodName)
                    .parameterTypes(parameterTypes)
                    .parameters(args)
                    .build();
            return request;
        }

        /**
         * 发起RPC请求
         *
         * @param host
         * @param port
         * @param timeout
         * @param request
         * @return
         */
        private Object doRequest(String host, int port,
                                 int timeout, EasyRpcRequest request,
                                 ProtocolEnum protocol) {
            try {
                EasyRpcResponse response = pool.sendRequest(host, port, timeout, protocol, request);
                if (Objects.isNull(response) || Objects.nonNull(response.getError())) {
                    LOGGER.error("response error, error:{}", response.getError());
                    return null;
                }
                return response.getResult();
            } catch (Throwable e) {
                LOGGER.error("request error, request:{}", request, e);
                return null;
            }
        }
    }

}
