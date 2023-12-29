package com.netease;

import com.netease.core.annotation.EasyRpcApi;

/**
 * @author zhuhai
 * @date 2023/12/25
 */
public interface HelloService {
    @EasyRpcApi(host = "localhost", port = 8888, provider = "com.netease.HelloServiceImpl")
    String sayHello(String name);
}
