package com.netease.core.annotation;

import java.lang.annotation.*;

/**
 * @author zhuhai
 * @date 2023/12/25
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EasyRpcProvider {

}
