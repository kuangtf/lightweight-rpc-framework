package com.ktf.rpc.core.serialization;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.IOException;

/**
 * @author github.com/kuangtf
 * @date 2021/10/15 12:47
 */
public class ProtostuffSerialization implements RpcSerialization{

    private static final LinkedBuffer BUFFER = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    @Override
    public <T> byte[] serialize(T obj) {
        Class<?> clazz = obj.getClass();
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(clazz);
        byte[] bytes;
        try {
            bytes = ProtostuffIOUtil.toByteArray(obj, schema, BUFFER);
        } finally {
            BUFFER.clear();
        }
        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        Schema<T> schema = RuntimeSchema.getSchema(clz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }
}
