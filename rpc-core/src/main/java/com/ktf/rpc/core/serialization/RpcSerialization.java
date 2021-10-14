package com.ktf.rpc.core.serialization;

import java.io.IOException;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 序列化接口
 */
public interface RpcSerialization {

    <T> byte[] serialize(T obj) throws IOException;

    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;
}