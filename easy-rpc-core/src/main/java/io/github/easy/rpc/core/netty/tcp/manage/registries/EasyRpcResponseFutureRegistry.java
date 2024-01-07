package io.github.easy.rpc.core.netty.tcp.manage.registries;

import io.github.easy.rpc.core.util.EasyRpcConstants;
import io.github.easy.rpc.core.bean.EasyRpcResponseFuture;
import io.github.easy.rpc.core.exception.EasyRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhuhai
 * @date 2023/12/21
 */
public class EasyRpcResponseFutureRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyRpcResponseFutureRegistry.class);
    private static Map<String, EasyRpcResponseFuture> responseFutureMap = new ConcurrentHashMap<>();


    /**
     * 添加请求
     *
     * @param requestId
     * @param responseFuture
     */
    public static void addResponseFuture(String requestId, EasyRpcResponseFuture responseFuture) {
        if (responseFutureMap.size() > EasyRpcConstants.MAX_REQUEST_SIZE) {
            LOGGER.error("easy-rpc netty client request size is max, size:{}", responseFutureMap.size());
            throw new EasyRpcException("easy-rpc netty client request size is max, size:" + responseFutureMap.size());
        }
        responseFutureMap.put(requestId, responseFuture);
    }

    public static EasyRpcResponseFuture getResponseFuture(String requestId) {
        return responseFutureMap.get(requestId);
    }

    public static EasyRpcResponseFuture removeResponseFuture(String requestId) {
        return responseFutureMap.remove(requestId);
    }
}
