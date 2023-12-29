package com.netease.easy.rpc.spring.config;

import com.netease.easy.rpc.core.annotation.EasyRpcProvider;
import com.netease.easy.rpc.core.exception.EasyRpcException;
import com.netease.easy.rpc.core.netty.tcp.manage.registries.ServiceProviderRegistry;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import java.util.Map;

/**
 * @author zhuhai
 * @date 2023/12/26
 */
public class AnnotationServiceProviderRegistry extends ServiceProviderRegistry implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(EasyRpcProvider.class);
        if (providers != null && providers.size() > 0) {
            for (Object provider : providers.values()) {
                // register provider
                if (provider.getClass().isInterface()) {
                    throw new EasyRpcException("provider must be a class");
                }
                register(provider);
            }
        }
    }
}