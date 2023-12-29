package com.netease.spring.config;

import com.netease.core.exception.EasyRpcException;
import com.netease.core.proxy.EasyRpcInvoker;
import com.netease.spring.bean.EasyRpcInvokerFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import java.util.Set;

/**
 * @author zhuhai
 * @date 2023/12/26
 */
public class ClassPathEasyRpcScanner extends ClassPathBeanDefinitionScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassPathEasyRpcScanner.class);
    private EasyRpcInvoker easyRpcInvoker;
    public ClassPathEasyRpcScanner(BeanDefinitionRegistry registry, EasyRpcInvoker easyRpcInvoker) {
        super(registry);
        this.easyRpcInvoker = easyRpcInvoker;
    }

    public void register(){
        addIncludeFilter((metadataReader, metadataReaderFactory) -> {
            // 只支持接口
            return metadataReader.getClassMetadata().isInterface();
        });
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        for(BeanDefinitionHolder holder : beanDefinitionHolders){
            GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();

            try {
                Class<?> clazz = Class.forName(definition.getBeanClassName());
                definition.getPropertyValues().add("targetClass", clazz);
                definition.getPropertyValues().add("easyRpcInvoker", easyRpcInvoker);
                definition.setBeanClass(EasyRpcInvokerFactoryBean.class);
            } catch (ClassNotFoundException e) {
                LOGGER.error("class not found! className [{}]", definition.getBeanClassName(), e);
                throw new EasyRpcException("class not found!");
            }
        }
        return beanDefinitionHolders;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
    }
}
