package com.netease.easy.rpc.service;

import com.netease.easy.rpc.core.annotation.EasyRpcProvider;
import org.springframework.stereotype.Service;

/**
 * @author zhuhai
 * @date 2023/12/26
 */

@EasyRpcProvider
public class HelloService {
    public String sayHello(String name) {
        return "hello " + name;
    }
}
