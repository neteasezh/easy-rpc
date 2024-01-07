package io.github.easy.rpc.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author zhuhai
 * @date 2023/12/26
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(EasyRpcScannerRegistrar.class)
public @interface EasyRpcScan {
    String[] value() default {};

    String[] basePackages() default {};


    Class<?>[] basePackageClasses() default {};
}
