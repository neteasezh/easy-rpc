package com.github.easy.rpc.autoconfigure;

import com.github.easy.rpc.annotation.EasyRpcFactoryBeanRegistrar;
import com.github.easy.rpc.core.config.EasyRpcProperties;
import com.github.easy.rpc.core.config.RegistryConfig;
import com.github.easy.rpc.core.enums.ProtocolEnum;
import com.github.easy.rpc.core.exception.EasyRpcException;
import com.github.easy.rpc.core.netty.base.AbstractEasyRpcServer;
import com.github.easy.rpc.core.netty.http.server.EasyRpcHttpServer;
import com.github.easy.rpc.core.netty.tcp.manage.registries.ServiceProviderInstanceRegistry;
import com.github.easy.rpc.core.netty.tcp.pool.EasyRpcClientPool;
import com.github.easy.rpc.core.netty.tcp.server.EasyRpcServer;
import com.github.easy.rpc.core.proxy.EasyRpcInvoker;
import com.github.easy.rpc.spring.config.AnnotationServiceProviderInstanceRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import java.util.List;

/**
 * @author zhuhai
 * @date 2023/12/26
 */

@Configuration
public class EasyRpcAutoConfiguration  {

    @Bean("easyRpcProperties")
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "easy-rpc")
    public EasyRpcProperties easyRpcProperties() {
        return new EasyRpcProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    AnnotationServiceProviderInstanceRegistry serviceProviderRegistry() {
        AnnotationServiceProviderInstanceRegistry registry = new AnnotationServiceProviderInstanceRegistry();
        return registry;
    }

    @Bean
    @ConditionalOnMissingBean
    public AbstractEasyRpcServer easyRpcServer(ServiceProviderInstanceRegistry registry, EasyRpcProperties easyRpcProperties) {
        AbstractEasyRpcServer easyRpcServer;
        if (ProtocolEnum.TCP.name().equalsIgnoreCase(easyRpcProperties.getProtocol())) {
            easyRpcServer = new EasyRpcServer(registry, easyRpcProperties);
        } else if (ProtocolEnum.HTTP.name().equalsIgnoreCase(easyRpcProperties.getProtocol())) {
            easyRpcServer = new EasyRpcHttpServer(registry, easyRpcProperties);
        } else {
            throw new EasyRpcException("protocol is not support");
        }
        if (easyRpcProperties.getEnableServer()) {
            // 异步启动
            easyRpcServer.openAsync();
        }
        return easyRpcServer;
    }


    @Bean
    @ConditionalOnMissingBean
    public EasyRpcInvoker easyRpcInvoker(EasyRpcProperties easyRpcProperties, ObjectProvider<List<RegistryConfig>> registries) {
        EasyRpcInvoker easyRpcInvoker = new EasyRpcInvoker(new EasyRpcClientPool(easyRpcProperties), registries.getIfAvailable());
        return easyRpcInvoker;
    }

    @Bean
    @ConditionalOnMissingBean
    public EasyRpcFactoryBeanRegistrar easyRpcFactoryBeanRegistrar(EasyRpcInvoker easyRpcInvoker, ApplicationContext applicationContext) {
        EasyRpcFactoryBeanRegistrar beanRegistrar = new EasyRpcFactoryBeanRegistrar(easyRpcInvoker, applicationContext);
        beanRegistrar.registerFactoryBeans();
        return beanRegistrar;
    }
}

