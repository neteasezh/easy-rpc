package com.github.easy.rpc;

import com.github.easy.rpc.annotation.EasyRpcScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhuhai
 * @date 2023/12/26
 */
@EasyRpcScan
@SpringBootApplication
public class EasyRpcClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyRpcClientApplication.class, args);
    }
}
