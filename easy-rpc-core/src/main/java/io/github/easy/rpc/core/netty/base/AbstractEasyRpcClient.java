package io.github.easy.rpc.core.netty.base;

import io.github.easy.rpc.core.bean.EasyRpcRequest;
import io.github.easy.rpc.core.bean.EasyRpcResponse;
import io.github.easy.rpc.core.config.EasyRpcProperties;
import io.github.easy.rpc.core.netty.tcp.client.EasyRpcClient;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import java.util.Objects;


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
    public abstract EasyRpcResponse sendRequest(EasyRpcRequest request, Long timeout);

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
