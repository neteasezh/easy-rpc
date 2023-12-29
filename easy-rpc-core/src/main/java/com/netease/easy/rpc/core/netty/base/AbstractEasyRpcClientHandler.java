package com.netease.easy.rpc.core.netty;

import com.netease.easy.rpc.core.bean.HeartBeat;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhuhai
 * @date 2023/12/28
 */
public abstract class AbstractEasyRpcClientHandler<T> extends SimpleChannelInboundHandler<T> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractEasyRpcClientHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;
        if (event.state() == IdleState.WRITER_IDLE) {
            ctx.channel().writeAndFlush(HeartBeat.request).sync();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("client caught exception...", cause);
        ctx.close();
    }
}
