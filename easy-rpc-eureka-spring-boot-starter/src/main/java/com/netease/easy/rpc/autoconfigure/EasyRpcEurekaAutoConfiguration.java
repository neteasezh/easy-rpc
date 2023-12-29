package com.netease.easy.rpc.autoconfigure;

import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhuhai
 * @date 2023/12/29
 */
@Configuration
@ConditionalOnClass({DiscoveryClient.class})
@AutoConfigureBefore(name = {"com.netease.easy.rpc.autoconfigure.EasyRpcAutoConfiguration"})
@AutoConfigureAfter(name = {"org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration"})
public class EasyRpcEurekaAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public EurekaProviderRegistryConfigRegistrar eurekaProviderRegistryConfigRegistrar(EurekaClient eurekaClient, ApplicationContext applicationContext) {
        EurekaProviderRegistryConfigRegistrar registrar = new EurekaProviderRegistryConfigRegistrar(eurekaClient, applicationContext);
        registrar.registerProviderRegistry();
        return registrar;
    }

}
