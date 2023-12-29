package com.netease.easy.rpc.core.netty;

import com.netease.easy.rpc.core.bean.EasyRpcRequest;
import com.netease.easy.rpc.core.bean.EasyRpcResponse;
import com.netease.easy.rpc.core.bean.EasyRpcResponseFuture;
import com.netease.easy.rpc.core.config.EasyRpcProperties;
import com.netease.easy.rpc.core.exception.EasyRpcException;
import com.netease.easy.rpc.core.netty.tcp.client.EasyRpcClient;
import com.netease.easy.rpc.core.netty.tcp.manage.registries.EasyRpcResponseFutureRegistry;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuhai
 * @date 2023/12/28
 */
public abstract class AbstractEasyRpcClient {
    protected NioEventLoopGroup eventLoopGroup;
    protected Channel channel;

    protected EasyRpcProperties properties;


    protected AbstractEasyRpcClient(EasyRpcProperties properties) {
        this.properties = properties;
    }

    /**
     * 初始化连接
     */
    public abstract void open(String host, int port);

    /**
     * 发送请求
     *
     * @param request
     * @param timeout
     * @return
     */
    public EasyRpcResponse sendRequest(EasyRpcRequest request, long timeout) {
        String requestId = request.getRequestId();
        EasyRpcResponseFuture responseFuture = new EasyRpcResponseFuture(request);
        EasyRpcResponseFutureRegistry.addResponseFuture(requestId, responseFuture);
        try {
            if (!isActive()) {
                throw new EasyRpcException("rpc client is not active");
            }
            this.channel.writeAndFlush(request).sync();
            return responseFuture.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            throw new EasyRpcException("send request error");
        } finally {
            EasyRpcResponseFutureRegistry.removeResponseFuture(requestId);
        }
    }

    /**
     * 初始化eventLoopGroup
     */
    protected void initEventLoopGroup() {
        if (eventLoopGroup == null) {
            synchronized (EasyRpcClient.class) {
                if (eventLoopGroup == null) {
                    eventLoopGroup = new NioEventLoopGroup();
                }
            }
        }
    }

    /**
     * 判断连接是否有效
     *
     * @return
     */
    public boolean isActive() {
        return Objects.nonNull(channel) && channel.isActive();
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (Objects.nonNull(channel) && channel.isActive()) {
            channel.close();
        }
        if (Objects.nonNull(eventLoopGroup)) {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public NioEventLoopGroup getEventLoopGroup() {
        return eventLoopGroup;
    }

    public void setEventLoopGroup(NioEventLoopGroup eventLoopGroup) {
        this.eventLoopGroup = eventLoopGroup;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public EasyRpcProperties getProperties() {
        return properties;
    }

    public void setProperties(EasyRpcProperties properties) {
        this.properties = properties;
    }
}
