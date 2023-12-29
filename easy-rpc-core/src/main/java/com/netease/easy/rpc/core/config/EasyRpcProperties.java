package com.netease.easy.rpc.core.config;

import com.netease.easy.rpc.core.bean.HeartBeat;
import io.netty.util.NettyRuntime;

/**
 * @author zhuhai
 * @date 2023/12/21
 */
public class EasyRpcConfig {
    private Integer connectTimeout = 3000;
    private Integer heartbeatInterval = HeartBeat.BEAT_INTERVAL;
    private Integer serverPort = 8888;
    private Integer serverBossWorkerThreads = 1;
    private Integer serverWorkThreads = NettyRuntime.availableProcessors();
    private Integer serverMessageProcessorCoreSize = Runtime.getRuntime().availableProcessors();
    private Integer serverMessageProcessorMaxSize = Runtime.getRuntime().availableProcessors() * 2;

    private Boolean enableServer = true;


    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(Integer heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public Integer getServerBossWorkerThreads() {
        return serverBossWorkerThreads;
    }

    public void setServerBossWorkerThreads(Integer serverBossWorkerThreads) {
        this.serverBossWorkerThreads = serverBossWorkerThreads;
    }

    public Integer getServerWorkThreads() {
        return serverWorkThreads;
    }

    public void setServerWorkThreads(Integer serverWorkThreads) {
        this.serverWorkThreads = serverWorkThreads;
    }

    public Integer getServerMessageProcessorCoreSize() {
        return serverMessageProcessorCoreSize;
    }

    public void setServerMessageProcessorCoreSize(Integer serverMessageProcessorCoreSize) {
        this.serverMessageProcessorCoreSize = serverMessageProcessorCoreSize;
    }

    public Integer getServerMessageProcessorMaxSize() {
        return serverMessageProcessorMaxSize;
    }

    public void setServerMessageProcessorMaxSize(Integer serverMessageProcessorMaxSize) {
        this.serverMessageProcessorMaxSize = serverMessageProcessorMaxSize;
    }

    public Boolean getEnableServer() {
        return enableServer;
    }

    public void setEnableServer(Boolean enableServer) {
        this.enableServer = enableServer;
    }
}
