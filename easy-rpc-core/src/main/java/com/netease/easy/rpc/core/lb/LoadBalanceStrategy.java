package com.netease.core.lb;

import com.netease.core.config.RegistryConfig;
import java.util.List;

/**
 * @author zhuhai
 * @date 2023/12/22
 */
public abstract class LoadBalanceStrategy {
    /**
     * 路由选择
     * @param appName
     * @param registries
     * @return
     */
    public abstract RegistryConfig select(String appName, List<RegistryConfig> registries);
}
