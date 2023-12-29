package com.netease.easy.rpc.core.netty.tcp.manage.registries;

import com.netease.easy.rpc.core.exception.EasyRpcException;
import com.sun.istack.internal.NotNull;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhuhai
 * @date 2023/12/20
 */
public class ServiceProviderInstanceRegistry {
    /**
     * 服务提供者类的全限定名: 服务提供者实例
     * spring容器启动时，将所有的服务提供者注册到该map中
     */
    private Map<String, Object> serviceProviderMap = new ConcurrentHashMap<>();

    /**
     * 注册服务提供者
     * @param provider
     */
    public void register(@NotNull Object provider) {
        if (Objects.isNull(provider)) {
            throw new EasyRpcException("service provider provided is null");
        }
        serviceProviderMap.put(provider.getClass().getName(), provider);
    }

    /**
     * 获取服务提供者
     * @param className
     * @return
     */
    public Object getServiceProvider(String className) {
        return serviceProviderMap.get(className);
    }
}
