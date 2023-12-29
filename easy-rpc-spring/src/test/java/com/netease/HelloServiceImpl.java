package com.netease;

import com.netease.core.annotation.EasyRpcProvider;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author zhuhai
 * @date 2023/12/21
 */
public class HelloServiceImpl {
    public String sayHello(String name) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "hello," + name;
    }
}
