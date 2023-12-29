package com.netease.core.annotation;

import com.netease.core.enums.LoadBalanceEnum;
import java.lang.annotation.*;

/**
 * @author zhuhai
 * @date 2023/12/22
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EasyRpcApi {
    String appName() default "";

    String host() default "";

    int port() default 0;

    int timeout() default 10000;

    LoadBalanceEnum lb() default LoadBalanceEnum.ROUND_ROBIN;

    String provider() default "";
}
