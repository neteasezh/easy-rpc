package io.github.easy.rpc.spring.config;

import com.github.easy.rpc.core.annotation.EasyRpcApi;
import com.github.easy.rpc.core.annotation.EasyRpcProvider;
import com.github.easy.rpc.core.exception.EasyRpcException;
import com.github.easy.rpc.core.proxy.EasyRpcInvoker;
import io.github.easy.rpc.spring.bean.EasyRpcInvokerFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.AnnotationMetadataReadingVisitor;
import org.springframework.util.CollectionUtils;
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

    public void register() {
        addIncludeFilter((metadataReader, metadataReaderFactory) -> {
            // 只支持接口
            AnnotationMetadataReadingVisitor classMetadata = (AnnotationMetadataReadingVisitor) metadataReader.getClassMetadata();
            return isCandidate(classMetadata);
        });
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        for (BeanDefinitionHolder holder : beanDefinitionHolders) {
            ScannedGenericBeanDefinition definition = (ScannedGenericBeanDefinition) holder.getBeanDefinition();
            AnnotationMetadata metadata = definition.getMetadata();
            try {
                if (!metadata.hasAnnotation(EasyRpcProvider.class.getName())) {
                    Class<?> clazz = Class.forName(definition.getBeanClassName());
                    definition.getPropertyValues().add("targetClass", clazz);
                    definition.getPropertyValues().add("easyRpcInvoker", easyRpcInvoker);
                    definition.setBeanClass(EasyRpcInvokerFactoryBean.class);
                }
            } catch (ClassNotFoundException e) {
                LOGGER.error("class not found! className [{}]", definition.getBeanClassName(), e);
                throw new EasyRpcException("class not found!");
            }
        }
        return beanDefinitionHolders;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return isCandidate(beanDefinition.getMetadata());
    }

    private boolean isCandidate(AnnotationMetadata metadata) {
        Set<MethodMetadata> set = metadata.getAnnotatedMethods(EasyRpcApi.class.getName());
        return (metadata.isInterface() && !metadata.isAnnotation() && !CollectionUtils.isEmpty(set))
                ||metadata.hasAnnotation(EasyRpcProvider.class.getName());
    }
}
