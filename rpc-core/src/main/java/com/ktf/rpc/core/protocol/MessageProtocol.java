package com.ktf.rpc.core.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 消息协议
 */
@Data
public class MessageProtocol<T> implements Serializable {

    /**
     *  消息头
     */
    private MessageHeader header;

    /**
     *  消息体，就是请求或响应的数据
     */
    private T body;

}
