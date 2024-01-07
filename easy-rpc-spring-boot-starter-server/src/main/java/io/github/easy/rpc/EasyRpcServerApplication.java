package io.github.easy.rpc;


import io.github.easy.rpc.annotation.EasyRpcScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhuhai
 * @date 2023/12/26
 */
@SpringBootApplication
@EasyRpcScan
public class EasyRpcServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyRpcServerApplication.class, args);
    }
}
