package io.github.easy.rpc.core.lb.impl;

import io.github.easy.rpc.core.config.RegistryConfig;
import io.github.easy.rpc.core.exception.EasyRpcException;
import io.github.easy.rpc.core.lb.LoadBalanceStrategy;

import java.util.List;
import java.util.Random;

/**
 * @author zhuhai
 * @date 2023/12/22
 */
public class RandomLoadBalanceStrategy extends LoadBalanceStrategy {
    private Random random = new Random();

    @Override
    public RegistryConfig select(String appName, List<RegistryConfig> registries) {
        if (registries == null || registries.size() == 0) {
            throw new EasyRpcException("no registry config available");
        }
        int index = random.nextInt(registries.size());
        return registries.get(index);
    }
}
