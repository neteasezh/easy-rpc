package io.github.easy.rpc.core.config;

import io.github.easy.rpc.core.bean.HeartBeat;
import io.github.easy.rpc.core.enums.ProtocolEnum;
import io.netty.util.NettyRuntime;

/**
 * @author zhuhai
 * @date 2023/12/21
 */
public class EasyRpcProperties {
    /**
     * 客户端连接超时时间
     */
    private Integer connectTimeout;

    /**
     * 心跳间隔
     */
    private Integer heartbeatInterval;

    /**
     * 客户端连接池大小
     */
    private Integer clientPoolSize;

    /**
     * 服务端端口
     */
    private Integer serverPort;

    /**
     * 服务端boss线程数
     */
    private Integer serverBossWorkerThreads;

    /**
     * 服务端work线程数
     */
    private Integer serverWorkThreads;

    /**
     * 服务端消息处理线程池核心线程数
     */
    private Integer serverMessageProcessorCoreSize;

    /**
     * 服务端消息处理线程池最大线程数
     */
    private Integer serverMessageProcessorMaxSize;

    /**
     * 是否启动服务端
     */
    private Boolean enableServer;

    /**
     * http最大内容长度
     */
    private Integer httpMaxContentLength;

    /**
     * 协议
     */
    private String protocol;

    public EasyRpcProperties() {
        this.connectTimeout = 3000;
        this.heartbeatInterval = HeartBeat.BEAT_INTERVAL;
        this.clientPoolSize = 100;
        this.serverPort = 8888;
        this.serverBossWorkerThreads = 1;
        this.serverWorkThreads = NettyRuntime.availableProcessors();
        this.serverMessageProcessorCoreSize = NettyRuntime.availableProcessors();
        this.serverMessageProcessorMaxSize = NettyRuntime.availableProcessors() * 2;
        this.enableServer = true;
        this.protocol = ProtocolEnum.TCP.name().toLowerCase();
        this.httpMaxContentLength = 10 * 1024 * 1024;
    }

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

    public Integer getClientPoolSize() {
        return clientPoolSize;
    }

    public void setClientPoolSize(Integer clientPoolSize) {
        this.clientPoolSize = clientPoolSize;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getHttpMaxContentLength() {
        return httpMaxContentLength;
    }

    public void setHttpMaxContentLength(Integer httpMaxContentLength) {
        this.httpMaxContentLength = httpMaxContentLength;
    }
}
