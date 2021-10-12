package com.ktf.rpc.core.protocol;

import lombok.Getter;

public enum MsgType {
    REQUEST((byte) 1),
    RESPONSE((byte) 2);

    @Getter
    private byte type;

    MsgType(byte type) {
        this.type = type;
    }

    public static MsgType findByType(byte type) {
        for (MsgType msgType : MsgType.values()) {
            if (msgType.getType() == type) {
                return msgType;
            }
        }
        return null;
    }
}
