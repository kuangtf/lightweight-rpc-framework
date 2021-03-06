package com.ktf.rpc.core.codec;

import com.ktf.rpc.core.protocol.MessageHeader;
import com.ktf.rpc.core.protocol.MessageProtocol;
import com.ktf.rpc.core.serialization.RpcSerialization;
import com.ktf.rpc.core.serialization.SerializationFactory;
import com.ktf.rpc.core.serialization.SerializationTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 自定义编码器，可以解决拆粘包问题
 */
@Slf4j
public class RpcEncoder<T> extends MessageToByteEncoder<MessageProtocol<T>> {

    /**
     *
     *  +---------------------------------------------------------------+
     *  | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte    |
     *  +---------------------------------------------------------------+
     *  | 状态 1byte |        消息 ID 32byte     |      数据长度 4byte     |
     *  +---------------------------------------------------------------+
     *  |                   数据内容 （长度不定）                           |
     *  +---------------------------------------------------------------+
     *
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageProtocol<T> messageProtocol, ByteBuf out) throws Exception {

        // 获取消息头
        MessageHeader header = messageProtocol.getHeader();

        // 魔数
        out.writeShort(header.getMagic());

        // 协议版本号
        out.writeByte(header.getVersion());

        // 序列化算法
        out.writeByte(header.getSerialization());

        // 报文类型
        out.writeByte(header.getMsgType());

        // 状态
        out.writeByte(header.getStatus());

        // 消息 ID
        out.writeCharSequence(header.getRequestId(), StandardCharsets.UTF_8);

        // 根据消息头中的序列化算法选择对应的序列化方式对消息体进行序列化
        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(SerializationTypeEnum.parseByType(header.getSerialization()));
        byte[] data = rpcSerialization.serialize(messageProtocol.getBody());

        // 数据长度
        out.writeInt(data.length);

        // 数据内容
        out.writeBytes(data);
    }
}
