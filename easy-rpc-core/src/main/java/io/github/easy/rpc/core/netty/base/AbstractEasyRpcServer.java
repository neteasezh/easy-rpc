package io.github.easy.rpc.core.netty.base;

import io.github.easy.rpc.core.config.EasyRpcProperties;
import io.github.easy.rpc.core.enums.ChannelState;
import io.github.easy.rpc.core.exception.EasyRpcException;
import io.github.easy.rpc.core.netty.tcp.manage.registries.ServiceProviderInstanceRegistry;
import io.github.easy.rpc.core.netty.tcp.server.EasyRpcServer;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuhai
 * @date 2023/12/28
 */
public abstract class AbstractEasyRpcServer {
    protected volatile ChannelState state = ChannelState.UN_INIT;
    protected EventLoopGroup bossGroup;
    protected EventLoopGroup workerGroup;
    protected Channel serverChannel;
    protected ThreadPoolExecutor requestExecutor;
    protected ThreadPoolExecutor localMethodExecutor;

    protected EasyRpcProperties properties;
    protected ServiceProviderInstanceRegistry serviceProviderInstanceRegistry;


    protected AbstractEasyRpcServer(ServiceProviderInstanceRegistry serviceProviderInstanceRegistry, EasyRpcProperties properties) {
        this.serviceProviderInstanceRegistry = serviceProviderInstanceRegistry;
        this.properties = properties;
    }

    /**
     * 同步启动
     */
    public abstract void openSync();

    /**
     * 异步启动
     */
    public abstract void openAsync();


    /**
     * 初始化requestExecutor
     */
    protected void initRequestExecutor() {
        if (Objects.isNull(requestExecutor)) {
            synchronized (EasyRpcServer.class) {
                if (Objects.isNull(requestExecutor)) {
                    requestExecutor
                            = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new DefaultThreadFactory("easy-rpc-server-handler", true));
                }
            }
        }
    }

    /**
     * 双重检验锁单例模式获取bossGroup/workerGroup
     */
    protected void initEventLoopGroup() {
        if (Objects.isNull(bossGroup) || Objects.isNull(workerGroup)) {
            synchronized (EasyRpcServer.class) {
                if (Objects.isNull(bossGroup) || Objects.isNull(workerGroup)) {
                    bossGroup = new NioEventLoopGroup(properties.getServerBossWorkerThreads());
                    workerGroup = new NioEventLoopGroup(properties.getServerBossWorkerThreads());
                }
            }
        }
    }


    /**
     * 双重检验锁单例模式获取localMethodExecutor
     */
    protected void initLocalMethodExecutor() {
        if (Objects.isNull(localMethodExecutor)) {
            synchronized (EasyRpcServer.class) {
                if (Objects.isNull(localMethodExecutor)) {
                    localMethodExecutor = new ThreadPoolExecutor(properties.getServerMessageProcessorCoreSize(), properties.getServerMessageProcessorCoreSize(), 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10000), new DefaultThreadFactory("easy-rpc-server", true), new ThreadPoolExecutor.AbortPolicy());
                    localMethodExecutor.prestartAllCoreThreads();
                }
            }
        }
    }

    public void close() {
        if (state.isCloseState()) {
            return;
        }
        try {
            stop();
            if (state.isUnInitState()) {
                return;
            }
            state = ChannelState.CLOSE;
        } catch (Exception e) {
            throw new EasyRpcException("easy-rpc server close error");
        }
    }

    /**
     * 关闭server
     */
    protected void stop() {
        if (Objects.nonNull(serverChannel)) {
            serverChannel.close();
        }
        if (Objects.nonNull(bossGroup)) {
            bossGroup.shutdownGracefully();
        }
        if (Objects.nonNull(workerGroup)) {
            workerGroup.shutdownGracefully();
        }
        if (Objects.nonNull(localMethodExecutor)) {
            localMethodExecutor.shutdown();
        }
        if (Objects.nonNull(requestExecutor)) {
            requestExecutor.shutdown();
        }
    }

    public ChannelState getState() {
        return state;
    }

    public void setState(ChannelState state) {
        this.state = state;
    }

    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public void setBossGroup(EventLoopGroup bossGroup) {
        this.bossGroup = bossGroup;
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public void setWorkerGroup(EventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
    }

    public Channel getServerChannel() {
        return serverChannel;
    }

    public void setServerChannel(Channel serverChannel) {
        this.serverChannel = serverChannel;
    }

    public EasyRpcProperties getProperties() {
        return properties;
    }

    public void setProperties(EasyRpcProperties properties) {
        this.properties = properties;
    }

    public ThreadPoolExecutor getRequestExecutor() {
        return requestExecutor;
    }

    public void setRequestExecutor(ThreadPoolExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
    }

    public ThreadPoolExecutor getLocalMethodExecutor() {
        return localMethodExecutor;
    }

    public void setLocalMethodExecutor(ThreadPoolExecutor localMethodExecutor) {
        this.localMethodExecutor = localMethodExecutor;
    }

    public ServiceProviderInstanceRegistry getServiceProviderRegistry() {
        return serviceProviderInstanceRegistry;
    }
}
