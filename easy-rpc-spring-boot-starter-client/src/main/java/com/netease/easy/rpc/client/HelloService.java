package com.netease.easy.rpc.client;

import com.netease.easy.rpc.core.annotation.EasyRpcApi;

/**
 * @author zhuhai
 * @date 2023/12/26
 */
public interface HelloService {
    @EasyRpcApi(host = "localhost", port = 9998, provider = "com.netease.easy.rpc.service.HelloService")
    String sayHello(String name);

}
