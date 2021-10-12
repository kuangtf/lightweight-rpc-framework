package com.ktf.rpc.core.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 *  消息协议
 */
@Data
public class MessageProtocol<T> implements Serializable {

    /**
     *  消息头
     */
    private MessageHeader header;

    /**
     *  消息体
     */
    private T body;

}
