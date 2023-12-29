package com.netease.easy.rpc;

import com.netease.easy.rpc.annotation.EasyRpcScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author zhuhai
 * @date 2023/12/26
 */
@EnableEurekaClient
@SpringBootApplication
@EasyRpcScan
public class EasyRpcServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyRpcServerApplication.class, args);
    }
}
