package com.netease.easy.rpc.core.annotation;

import com.netease.easy.rpc.core.enums.LoadBalanceEnum;
import com.netease.easy.rpc.core.enums.ProtocolEnum;
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

    ProtocolEnum protocol() default ProtocolEnum.TCP;
}
