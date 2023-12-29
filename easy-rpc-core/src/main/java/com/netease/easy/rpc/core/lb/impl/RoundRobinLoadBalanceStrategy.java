package com.netease.easy.rpc.core.lb.impl;

import com.netease.easy.rpc.core.config.RegistryConfig;
import com.netease.easy.rpc.core.exception.EasyRpcException;
import com.netease.easy.rpc.core.lb.LoadBalanceStrategy;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhuhai
 * @date 2023/12/22
 */
public class RoundRobinLoadBalanceStrategy extends LoadBalanceStrategy {
    private Map<String, AtomicLong> counterMap = new ConcurrentHashMap<>();
    private long timeOffset = System.currentTimeMillis();

    @Override

    public RegistryConfig select(String appName, List<RegistryConfig> registries) {
        if (registries == null || registries.size() == 0) {
            throw new EasyRpcException("no registry config available");
        }
        long curTs = System.currentTimeMillis();

        // 超过一天重置
        if (curTs - timeOffset > TimeUnit.DAYS.toMillis(1)) {
            counterMap.clear();
            timeOffset = curTs;
        }

        AtomicLong counter = counterMap.get(appName);
        if (Objects.isNull(counter)) {
            counter = new AtomicLong(0);
            counterMap.put(appName, counter);
        }
        int index = (int) (counter.getAndIncrement() % registries.size());
        return registries.get(index);
    }
}
