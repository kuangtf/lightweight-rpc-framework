package com.ktf.rpc.core.protocol;

import lombok.Getter;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 请求状态
 */
public enum MsgStatus {

    /**
     * 请求成功
     */
    SUCCESS((byte)0),

    /**
     * 请求失败
     */
    FAIL((byte)1);

    @Getter
    private final byte code;

    MsgStatus(byte code) {
        this.code = code;
    }

    public static boolean isSuccess(byte code){
        return MsgStatus.SUCCESS.code == code;
    }

}
