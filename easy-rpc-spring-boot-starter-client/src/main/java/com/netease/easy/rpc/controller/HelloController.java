package com.netease.easy.rpc.controller;

import com.netease.easy.rpc.client.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhuhai
 * @date 2023/12/26
 */

@RestController
public class HelloController {
    @Autowired
    private HelloService helloService;

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(String name) {
        return helloService.sayHello(name);
    }
}

