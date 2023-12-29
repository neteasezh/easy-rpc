package com.netease.easy.rpc.core.netty.pool;

import com.netease.easy.rpc.core.bean.EasyRpcRequestConfig;
import com.netease.easy.rpc.core.util.EasyRpcConstants;
import com.netease.easy.rpc.core.bean.EasyRpcRequest;
import com.netease.easy.rpc.core.bean.EasyRpcResponse;
import com.netease.easy.rpc.core.bean.EasyRpcResponseFuture;
import com.netease.easy.rpc.core.config.EasyRpcProperties;
import com.netease.easy.rpc.core.exception.EasyRpcException;
import com.netease.easy.rpc.core.netty.client.EasyRpcClient;
import com.netease.easy.rpc.core.netty.manage.registries.EasyRpcResponseFutureRegistry;
import com.netease.easy.rpc.core.util.LRUCache;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuhai
 * @date 2023/12/21
 */
public class EasyRpcClientPool {
    private Map<String, EasyRpcClient> clientPool;
    private EasyRpcProperties properties;

    public EasyRpcClientPool(EasyRpcProperties properties) {
        this.properties = properties;
        clientPool = new LRUCache<>(properties.getClientPoolSize());
    }

    /**
     * 发送请求
     *
     * @param request
     * @param requestConfig
     * @return
     * @throws Exception
     */
    public EasyRpcResponse sendRequest(EasyRpcRequest request, EasyRpcRequestConfig requestConfig) throws Exception {
        return doSendRequest(requestConfig.getHost(), requestConfig.getPort(), requestConfig.getTimeout(), request, this.properties);
    }


    /**
     * 发送请求
     *
     * @param host
     * @param port
     * @param timeout
     * @param request
     * @return
     * @throws Exception
     */
    public EasyRpcResponse sendRequest(String host, int port, long timeout, EasyRpcRequest request) throws Exception {
        return doSendRequest(host, port, timeout, request, properties);
    }


    /**
     * 获取rpc client并发送请求
     * @param host
     * @param port
     * @param timeout
     * @param request
     * @param properties
     * @return
     * @throws Exception
     */
    private EasyRpcResponse doSendRequest(String host, int port, long timeout, EasyRpcRequest request, EasyRpcProperties properties) throws Exception {
        String requestId = request.getRequestId();
        EasyRpcResponseFuture responseFuture = new EasyRpcResponseFuture(request);
        EasyRpcResponseFutureRegistry.addResponseFuture(requestId, responseFuture);
        try {
            EasyRpcClient rpcClient = getRpcClient(host, port, properties);
            if (!rpcClient.isActive()) {
                throw new EasyRpcException("rpc client is not active");
            }
            rpcClient.getChannel().writeAndFlush(request).sync();
            return responseFuture.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            throw new EasyRpcException("send request error", e);
        } finally {
            EasyRpcResponseFutureRegistry.removeResponseFuture(requestId);
        }
    }


    /**
     * 获取连接
     *
     * @param host
     * @param port
     * @return
     */
    private EasyRpcClient getRpcClient(String host, int port, EasyRpcProperties properties) throws Exception {
        String address = host + EasyRpcConstants.WELL_SYMBOL + port;
        EasyRpcClient easyRpcClient = clientPool.get(address);

        if (easyRpcClient == null || !easyRpcClient.isActive()) {
            synchronized (EasyRpcClient.class) {
                if (easyRpcClient == null || !easyRpcClient.isActive()) {
                    easyRpcClient = new EasyRpcClient(properties);
                    easyRpcClient.open(host, port);
                    clientPool.put(address, easyRpcClient);
                }
            }
        }
        return easyRpcClient;
    }

    public EasyRpcProperties getProperties() {
        return properties;
    }

    public void setProperties(EasyRpcProperties properties) {
        this.properties = properties;
    }
}
