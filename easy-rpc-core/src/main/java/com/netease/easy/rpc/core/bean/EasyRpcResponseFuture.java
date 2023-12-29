package com.netease.easy.rpc.core.bean;

import com.netease.easy.rpc.core.enums.FutureState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author zhuhai
 * @date 2023/12/20
 */
public class EasyRpcResponseFuture extends EasyRpcResponse implements Future<EasyRpcResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyRpcResponseFuture.class);
    private Object lock = new Object();
    private final EasyRpcRequest request;
    private volatile EasyRpcResponse response;
    private volatile FutureState state = FutureState.DOING;

    public EasyRpcResponseFuture(EasyRpcRequest request) {
        this.request = request;
    }

    @Override
    public boolean cancel(boolean canceled) {
        if (canceled) {
            synchronized (lock) {
                if (state.isDoingState()) {
                    return false;
                }
                state = FutureState.CANCELLED;
                lock.notifyAll();
            }
        }
        return true;
    }

    @Override
    public boolean isCancelled() {
        return state.isCancelledState();
    }

    @Override
    public boolean isDone() {
        return state.isDoneState();
    }

    @Override
    public EasyRpcResponse get() throws InterruptedException, ExecutionException {
        return doGet(0L, TimeUnit.MILLISECONDS);
    }

    @Override
    public EasyRpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return doGet(timeout, unit);
    }

    private EasyRpcResponse doGet(long timeout, TimeUnit timeUnit) {
        if (!state.isDoneState()) {
            synchronized (lock) {
                try {
                    if (timeout <= 0) {
                        lock.wait();
                    } else {
                        long timeoutMillis = (TimeUnit.MILLISECONDS == timeUnit) ? timeout : TimeUnit.MILLISECONDS.convert(timeout, timeUnit);
                        lock.wait(timeoutMillis);
                    }
                } catch (InterruptedException e) {
                    LOGGER.error("easy-rpc, get response error, requestId:" + request.getRequestId(), e);
                    cancel();
                }
            }
        }
        if (!state.isDoneState()) {
            LOGGER.error("easy-rpc, request timeout, requestId:" + request.getRequestId());
            cancel();
        }
        return response;
    }


    private boolean cancel() {
        synchronized (lock) {
            if (!state.isDoingState()) {
                return false;
            }
            state = FutureState.CANCELLED;
            lock.notifyAll();
        }
        return true;
    }

    public void setResponse(EasyRpcResponse response) {
        this.response = response;
        synchronized (lock) {
            state = FutureState.DONE;
            lock.notifyAll();
        }
    }
}
