package com.netease.core.bean;

import java.io.Serializable;

/**
 * @author zhuhai
 * @date 2023/12/19
 */
public class EasyRpcResponse implements Serializable {
    private static final long serialVersionUID = -7559175531498998053L;

    private String requestId;
    private Throwable error;
    private Object result;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
