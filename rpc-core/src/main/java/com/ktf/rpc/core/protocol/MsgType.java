package com.ktf.rpc.core.protocol;

import lombok.Getter;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 消息类型
 */
public enum MsgType {

    // 请求消息
    REQUEST((byte) 1),

    // 响应消息
    RESPONSE((byte) 2);

    @Getter
    private final byte type;

    MsgType(byte type) {
        this.type = type;
    }

    /**
     * 根据消息的编号返回对应的枚举类型
     */
    public static MsgType findByType(byte type) {
        for (MsgType msgType : MsgType.values()) {
            if (msgType.getType() == type) {
                return msgType;
            }
        }
        return null;
    }
}
