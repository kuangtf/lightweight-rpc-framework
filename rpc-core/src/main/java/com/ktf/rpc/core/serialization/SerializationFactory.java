package com.ktf.rpc.core.serialization;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 序列化工厂类
 */
public class SerializationFactory {

    /**
     * 根据传入的序列化算法创建不同的序列化对象
     * @param typeEnum 序列化算法
     * @return 返回一个序列化算法对象
     */
    public static RpcSerialization getRpcSerialization(SerializationTypeEnum typeEnum) {

        switch (typeEnum) {
            case HESSIAN:
                return new HessianSerialization();
            case JSON:
                return new JsonSerialization();
            case PROTOSTUFF:
                return new ProtostuffSerialization();
            case KRYO:
                return new KryoSerialization();
            case XML:
                return new XmlSerialization();
            default:
                throw new IllegalArgumentException("serialization type is illegal");
        }
    }

}
