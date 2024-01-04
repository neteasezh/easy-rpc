package com.github.easy.rpc.client;

import com.github.easy.rpc.core.annotation.EasyRpcApi;

/**
 * @author zhuhai
 * @date 2023/12/26
 */
public interface HelloService {
    @EasyRpcApi(host = "localhost", port = 9998, provider = "com.github.easy.rpc.service.HelloService")
    String sayHello(String name);

}
