package com.ktf.rpc.core.protocol;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 协议相关的常量
 */
public class ProtocolConstants {

    /**
     * 消息头总长度
     */
    public static final int HEADER_TOTAL_LEN = 42;

    /**
     * 魔数：用于校验这个消息是合法的
     */
    public static final short MAGIC = 0x00;

    /**
     * 版协版本号
     */
    public static final byte VERSION = 0x1;

    /**
     * 消息 ID
     */
    public static final int REQ_LEN = 32;

}
