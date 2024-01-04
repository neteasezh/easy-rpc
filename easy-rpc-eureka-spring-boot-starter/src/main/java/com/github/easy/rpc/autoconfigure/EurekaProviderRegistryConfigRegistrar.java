package com.github.easy.rpc.autoconfigure;

import com.github.easy.rpc.core.config.RegistryConfig;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhuhai
 * @date 2023/12/29
 */
public class EurekaProviderRegistryConfigRegistrar implements BeanPostProcessor {
    private static final String SYMBOL = "#";
    private EurekaClient eurekaClient;
    private ApplicationContext applicationContext;

    public EurekaProviderRegistryConfigRegistrar(EurekaClient eurekaClient, ApplicationContext applicationContext) {
        this.eurekaClient = eurekaClient;
        this.applicationContext = applicationContext;
    }

    public void registerProviderRegistry() {
        List<RegistryConfig> registryConfigs = getRegistryConfigs();
        List<RegistryConfig> configList = registryConfigs.stream().distinct().collect(Collectors.toList());
        ConfigurableApplicationContext cac = (ConfigurableApplicationContext) applicationContext;
        for (RegistryConfig registryConfig : configList) {
            String beanName = registryConfig.getAppName() + SYMBOL + registryConfig.getHost() + SYMBOL + registryConfig.getPort();
            cac.getBeanFactory().registerSingleton(beanName, registryConfig);
        }
    }

    private List<RegistryConfig> getRegistryConfigs() {
        List<RegistryConfig> registryConfigs = new ArrayList<>();
        List<Application> applications = eurekaClient.getApplications().getRegisteredApplications();
        List<InstanceInfo> instances = applications.stream().map(Application::getInstances).flatMap(List::stream).collect(Collectors.toList());
        instances.add(ApplicationInfoManager.getInstance().getInfo());
        for (InstanceInfo instance : instances) {
            RegistryConfig registryConfig = new RegistryConfig();
            registryConfig.setAppName(instance.getAppName());
            registryConfig.setHost(instance.getIPAddr());
            registryConfig.setPort(instance.getPort());
            registryConfigs.add(registryConfig);
        }
        return registryConfigs;
    }

}
