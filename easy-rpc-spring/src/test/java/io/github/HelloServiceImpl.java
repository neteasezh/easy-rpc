package io.github;

import io.github.easy.rpc.core.annotation.EasyRpcProvider;
import java.util.concurrent.TimeUnit;


/**
 * @author zhuhai
 * @date 2023/12/21
 */
@EasyRpcProvider
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
