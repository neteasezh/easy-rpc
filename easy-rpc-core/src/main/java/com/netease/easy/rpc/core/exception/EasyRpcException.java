package com.netease.easy.rpc.core.exception;

/**
 * @author zhuhai
 * @date 2023/12/19
 */
public class EasyRpcException extends RuntimeException {

    public EasyRpcException() {
    }

    public EasyRpcException(String message) {
        super(message);
    }

    public EasyRpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public EasyRpcException(Throwable cause) {
        super(cause);
    }

    public EasyRpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
