package io.github.easy.rpc.service;

import com.github.easy.rpc.core.annotation.EasyRpcProvider;

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
