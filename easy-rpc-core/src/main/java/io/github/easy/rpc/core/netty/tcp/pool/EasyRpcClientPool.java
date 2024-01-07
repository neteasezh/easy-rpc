package io.github.easy.rpc.core.netty.tcp.pool;

import io.github.easy.rpc.core.bean.EasyRpcRequestConfig;
import io.github.easy.rpc.core.enums.ProtocolEnum;
import io.github.easy.rpc.core.netty.base.AbstractEasyRpcClient;
import io.github.easy.rpc.core.netty.http.client.EasyRpcHttpClient;
import io.github.easy.rpc.core.netty.tcp.client.EasyRpcClient;
import io.github.easy.rpc.core.netty.tcp.manage.registries.EasyRpcResponseFutureRegistry;
import io.github.easy.rpc.core.util.EasyRpcConstants;
import io.github.easy.rpc.core.bean.EasyRpcRequest;
import io.github.easy.rpc.core.bean.EasyRpcResponse;
import io.github.easy.rpc.core.bean.EasyRpcResponseFuture;
import io.github.easy.rpc.core.config.EasyRpcProperties;
import io.github.easy.rpc.core.exception.EasyRpcException;
import io.github.easy.rpc.core.util.LRUCache;

import java.util.Map;


/**
 * @author zhuhai
 * @date 2023/12/21
 */
public class EasyRpcClientPool {
    private Map<String, AbstractEasyRpcClient> clientPool;
    private EasyRpcProperties properties;

    public EasyRpcClientPool() {
        this.properties = new EasyRpcProperties();
        clientPool = new LRUCache<>(properties.getClientPoolSize());
    }

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
        return doSendRequest(requestConfig.getProtocol(), requestConfig.getHost(), requestConfig.getPort(), requestConfig.getTimeout(), request, this.properties);
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
        return doSendRequest(ProtocolEnum.TCP, host, port, timeout, request, properties);
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
    public EasyRpcResponse sendRequest(String host, int port, long timeout, ProtocolEnum protocol, EasyRpcRequest request) throws Exception {
        return doSendRequest(protocol, host, port, timeout, request, properties);
    }


    /**
     * 获取rpc client并发送请求
     *
     * @param protocolEnum
     * @param host
     * @param port
     * @param timeout
     * @param request
     * @param properties
     * @return
     * @throws Exception
     */
    private EasyRpcResponse doSendRequest(ProtocolEnum protocolEnum, String host,
                                          int port, long timeout,
                                          EasyRpcRequest request, EasyRpcProperties properties) throws Exception {
        String requestId = request.getRequestId();
        EasyRpcResponseFuture responseFuture = new EasyRpcResponseFuture(request);
        EasyRpcResponseFutureRegistry.addResponseFuture(requestId, responseFuture);
        try {
            AbstractEasyRpcClient rpcClient = getRpcClient(protocolEnum, host, port, properties);
            if (!rpcClient.isActive()) {
                throw new EasyRpcException("rpc client is not active");
            }
            return rpcClient.sendRequest(request, timeout);
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
    private AbstractEasyRpcClient getRpcClient(ProtocolEnum protocolEnum, String host,
                                               int port, EasyRpcProperties properties) throws Exception {
        String address = host + EasyRpcConstants.WELL_SYMBOL + port;
        AbstractEasyRpcClient easyRpcClient = clientPool.get(address);

        if (easyRpcClient == null || !easyRpcClient.isActive()) {
            synchronized (EasyRpcClient.class) {
                if (easyRpcClient == null || !easyRpcClient.isActive()) {
                    if (ProtocolEnum.HTTP == protocolEnum) {
                        easyRpcClient = new EasyRpcHttpClient(properties);
                    } else {
                        easyRpcClient = new EasyRpcClient(properties);
                    }
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
