package com.netease.easy.rpc.spring.config;

import com.netease.easy.rpc.core.proxy.EasyRpcInvoker;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

/**
 * @author zhuhai
 * @date 2023/12/26
 */
public class EasyRpcScannerConfigure implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    private String basePackage;
    private EasyRpcInvoker easyRpcInvoker;
    private ApplicationContext applicationContext;


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ClassPathEasyRpcScanner scanner = new ClassPathEasyRpcScanner(registry, easyRpcInvoker);
        scanner.register();
        scanner.doScan(StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {}


    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public EasyRpcInvoker getEasyRpcInvoker() {
        return easyRpcInvoker;
    }

    public void setEasyRpcInvoker(EasyRpcInvoker easyRpcInvoker) {
        this.easyRpcInvoker = easyRpcInvoker;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
