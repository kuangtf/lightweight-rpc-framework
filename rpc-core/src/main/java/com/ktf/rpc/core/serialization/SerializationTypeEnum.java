package com.ktf.rpc.core.serialization;

import lombok.Getter;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 序列化算法枚举类，由消息头设置
 */
public enum SerializationTypeEnum {

    /**
     * 0 代表使用 HESSIAN 序列化，默认
     */
    HESSIAN((byte) 0),

    /**
     * 1 代表使用 JSON 序列化
     */
    JSON((byte) 1),

    /**
     * 2 代表使用 Protostuff 序列化
     */
    PROTOSTUFF((byte) 2),

    /**
     * 3 代表使用 KRYO 序列化
     */
    KRYO((byte) 3),

    /**
     * xml 序列化方式
     */
    XML((byte) 4);

    @Getter
    private final byte type;

    SerializationTypeEnum(byte type) {
        this.type = type;
    }

    /**
     * 根据传入的序列化名称返回对应的序列化类型
     */
    public static SerializationTypeEnum parseByName(String typeName) {
        for (SerializationTypeEnum typeEnum : SerializationTypeEnum.values()) {
            if (typeEnum.name().equalsIgnoreCase(typeName)) {
                return typeEnum;
            }
        }
        return PROTOSTUFF;
    }

    /**
     * 根据传入的序列化编号返回对应的序列化类型
     */
    public static SerializationTypeEnum parseByType(byte type) {
        for (SerializationTypeEnum typeEnum : SerializationTypeEnum.values()) {
            if (typeEnum.getType() == type) {
                return typeEnum;
            }
        }
        return PROTOSTUFF;
    }

}
