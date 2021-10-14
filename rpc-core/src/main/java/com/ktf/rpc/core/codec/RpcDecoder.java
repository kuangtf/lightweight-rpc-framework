package com.ktf.rpc.core.codec;

import com.ktf.rpc.core.common.RpcRequest;
import com.ktf.rpc.core.common.RpcResponse;
import com.ktf.rpc.core.protocol.MessageHeader;
import com.ktf.rpc.core.protocol.MessageProtocol;
import com.ktf.rpc.core.protocol.MsgType;
import com.ktf.rpc.core.protocol.ProtocolConstants;
import com.ktf.rpc.core.serialization.RpcSerialization;
import com.ktf.rpc.core.serialization.SerializationFactory;
import com.ktf.rpc.core.serialization.SerializationTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 自定义解码器
 */
@Slf4j
public class RpcDecoder extends ByteToMessageDecoder {

    /**
     *
     *  +---------------------------------------------------------------+
     *  | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte    |
     *  +---------------------------------------------------------------+
     *  | 状态 1byte |        消息 ID 32byte     |      数据长度 4byte      |
     *  +---------------------------------------------------------------+
     *  |                   数据内容 （长度不定）                           |
     *  +---------------------------------------------------------------+
     *
     *  decode 这个方法会被循环调用，直至确认没有新元素被添加到该 List 或 ByteBuf 没有可读字节为止
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        // 可读的数据小于请求头总的大小 直接丢弃
        if (in.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
            return;
        }
        // 标记 ByteBuf 读指针位置
        in.markReaderIndex();

        // 魔数，校验该数据是否合法
        short magic = in.readShort();
        if (magic != ProtocolConstants.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal, " + magic);
        }

        // 协议号版本
        byte version = in.readByte();
        // 序列化算法
        byte serializeType = in.readByte();
        // 报文类型
        byte msgType = in.readByte();
        // 状态
        byte status = in.readByte();
        // 消息 ID（直接从 ByteBuf 中获取20各个字节的数据作为请求 ID）
        CharSequence requestId = in.readCharSequence(ProtocolConstants.REQ_LEN, StandardCharsets.UTF_8);
        // 数据长度
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            // 可读的数据长度小于请求体长度 直接丢弃并重置 读指针位置
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        // 消息类型
        MsgType msgTypeEnum = MsgType.findByType(msgType);
        if (msgTypeEnum == null) {
            return;
        }
        // 封装消息头
        MessageHeader header = new MessageHeader();
        header.setMagic(magic);
        header.setVersion(version);
        header.setSerialization(serializeType);
        header.setStatus(status);
        header.setRequestId(String.valueOf(requestId));
        header.setMsgType(msgType);
        header.setMsgLen(dataLength);
        // 序列化对象
        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(SerializationTypeEnum.parseByType(serializeType));
        // 根据消息类型进行相应的反序列化
        switch (msgTypeEnum) {
            case REQUEST:
                RpcRequest request = rpcSerialization.deserialize(data, RpcRequest.class);
                if (request != null) {
                    MessageProtocol<RpcRequest> protocol = new MessageProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(request);
                    out.add(protocol);
                }
                break;
            case RESPONSE:
                RpcResponse response = rpcSerialization.deserialize(data, RpcResponse.class);
                if (response != null) {
                    MessageProtocol<RpcResponse> protocol = new MessageProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);
                    out.add(protocol);
                }
                break;
        }
    }
}
