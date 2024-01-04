package com.github.easy.rpc.annotation;

import com.github.easy.rpc.core.exception.EasyRpcException;
import com.github.easy.rpc.core.proxy.EasyRpcInvoker;
import com.github.easy.rpc.spring.config.ClassPathEasyRpcScanner;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;
import java.util.List;

/**
 * @author zhuhai
 * @date 2023/12/29
 */
public class EasyRpcFactoryBeanRegistrar implements  BeanPostProcessor {
    private ConfigurableApplicationContext applicationContext;
    private EasyRpcInvoker easyRpcInvoker;

    public EasyRpcFactoryBeanRegistrar(EasyRpcInvoker easyRpcInvoker, ApplicationContext applicationContext) {
        this.easyRpcInvoker = easyRpcInvoker;
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    public void registerFactoryBeans() {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        EasyRpcScannerRegistrar registrar = applicationContext.getBean(EasyRpcScannerRegistrar.class);
        ClassPathEasyRpcScanner scanner = new ClassPathEasyRpcScanner(registry, easyRpcInvoker);
        scanner.register();
        String basePackage = registrar.getBasePackage();
        if (StringUtils.isEmpty(basePackage)) {
            List<String> packages = AutoConfigurationPackages.get(applicationContext.getBeanFactory());
            basePackage = StringUtils.collectionToCommaDelimitedString(packages);
        }
        if (StringUtils.isEmpty(basePackage)) {
            throw new EasyRpcException("easy rpc is unable to obtain the package to be scanned");
        }
        scanner.doScan(basePackage);
    }
}
