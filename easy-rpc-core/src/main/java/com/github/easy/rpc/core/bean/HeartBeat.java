package com.github.easy.rpc.core.bean;

/**
 * @author zhuhai
 * @date 2023/12/20
 */
public class HeartBeat {
    public static final int BEAT_INTERVAL = 30;
    public static final String BEAT_MSG = "BEAT_PING_PONG";

    public static EasyRpcRequest request;

    static {
        request = new EasyRpcRequest();
        request.setRequestId(BEAT_MSG);
    }
}
