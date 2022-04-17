package com.ktf.rpc.core.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * JSON 格式的序列化方式
 */
public class JsonSerialization implements RpcSerialization {

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = generateMapper();
    }

    private static ObjectMapper generateMapper() {
        ObjectMapper customMapper = new ObjectMapper();

        customMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        customMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        customMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
        customMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        return customMapper;
    }

    /**
     * 序列化
     */
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        return MAPPER.writeValueAsBytes(obj);
    }

    /**
     * 反序列化
     */
    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        return MAPPER.readValue(data, clz);
    }
}
