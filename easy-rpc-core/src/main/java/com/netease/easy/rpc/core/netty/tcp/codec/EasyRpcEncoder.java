package com.netease.easy.rpc.core.netty.codec;

import com.netease.easy.rpc.core.util.HessianSerializerUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author zhuhai
 * @date 2023/12/19
 */
public class EasyRpcEncoder extends MessageToByteEncoder {
    private Class<?> genericClass;

    public EasyRpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf byteBuf) throws Exception {
        if (genericClass.isInstance(o)) {
            byte[] data = HessianSerializerUtil.serialize(o);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }
    }
}
