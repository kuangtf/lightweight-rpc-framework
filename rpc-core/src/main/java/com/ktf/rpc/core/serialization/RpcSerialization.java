package com.ktf.rpc.core.serialization;

import java.io.IOException;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 序列化接口
 */
public interface RpcSerialization {

    /**
     * 序列化接口
     * @param obj 对象
     * @param <T> 泛型
     * @return 序列化之后的字节数组
     * @throws IOException 异常
     */
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * 反序列化
     * @param data 字节数组
     * @param clz Class 类
     * @param <T> 泛型
     * @return 反序列化之后的对象
     * @throws IOException 异常
     */
    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;

}