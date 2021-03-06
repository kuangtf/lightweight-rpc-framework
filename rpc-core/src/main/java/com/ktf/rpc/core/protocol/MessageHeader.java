package com.ktf.rpc.core.protocol;

import com.ktf.rpc.core.serialization.SerializationTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 消息头
 */
@Data
public class MessageHeader implements Serializable {

    /*
    +---------------------------------------------------------------+
    | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte    |
    +---------------------------------------------------------------+
    | 状态 1byte |        消息 ID 32byte     |      数据长度 4byte     |
    +---------------------------------------------------------------+
    */
    /**
     *  魔数
     */
    private short magic;

    /**
     *  协议版本号
     */
    private byte version;

    /**
     *  序列化算法
     */
    private byte serialization;

    /**
     *  报文类型
     */
    private byte msgType;

    /**
     *  状态
     */
    private byte status;

    /**
     *  消息 ID
     */
    private String requestId;

    /**
     *  数据长度
     */
    private int msgLen;

    /**
     * 构建消息头
     */
    public static MessageHeader build(String serialization, int msgLen){
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setMagic(ProtocolConstants.MAGIC);
        messageHeader.setVersion(ProtocolConstants.VERSION);
        messageHeader.setSerialization(SerializationTypeEnum.parseByName(serialization).getType());
        messageHeader.setMsgType(MsgType.REQUEST.getType());
        messageHeader.setRequestId(UUID.randomUUID().toString().replaceAll("-",""));
        messageHeader.setMsgLen(msgLen);
        messageHeader.setStatus(MsgStatus.SUCCESS.getCode());
        return messageHeader;
    }
}
