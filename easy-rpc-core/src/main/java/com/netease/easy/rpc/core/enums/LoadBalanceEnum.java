package com.netease.easy.rpc.core.enums;

import com.netease.easy.rpc.core.lb.LoadBalanceStrategy;
import com.netease.easy.rpc.core.lb.impl.RandomLoadBalanceStrategy;
import com.netease.easy.rpc.core.lb.impl.RoundRobinLoadBalanceStrategy;

/**
 * @author zhuhai
 * @date 2023/12/22
 */
public enum LoadBalanceEnum {
    RANDOM(new RandomLoadBalanceStrategy()),
    ROUND_ROBIN(new RoundRobinLoadBalanceStrategy()),
    ;
    public final LoadBalanceStrategy strategy;

    LoadBalanceEnum(LoadBalanceStrategy strategy) {
        this.strategy = strategy;
    }

    public LoadBalanceStrategy getStrategy() {
        return strategy;
    }
}

