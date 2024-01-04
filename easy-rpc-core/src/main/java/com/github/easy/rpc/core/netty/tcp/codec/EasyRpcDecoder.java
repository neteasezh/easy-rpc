package com.github.easy.rpc.core.netty.tcp.codec;

import com.github.easy.rpc.core.util.HessianSerializerUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 * @author zhuhai
 * @date 2023/12/19
 */
public class EasyRpcDecoder extends ByteToMessageDecoder {
    private static final int INT_HEAD_LENGTH = 4;
    private Class<?> genericClass;

    public EasyRpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() < INT_HEAD_LENGTH){
            return;
        }
        byteBuf.markReaderIndex();
        int length = byteBuf.readInt();
        if (length < 0) {
            ctx.close();
        }
        if (byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Object obj = HessianSerializerUtil.deserialize(bytes, genericClass);
        list.add(obj);
    }
}
