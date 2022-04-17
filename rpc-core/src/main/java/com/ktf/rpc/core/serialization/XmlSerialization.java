package com.ktf.rpc.core.serialization;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author github.com/kuangtf
 * @date 2022/3/30 8:50
 */
public class XmlSerialization implements RpcSerialization {

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLEncoder xmlEncoder = new XMLEncoder(out, "utf-8", true, 0);
        xmlEncoder.writeObject(obj);
        xmlEncoder.close();
        return out.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        XMLDecoder xmlDecoder = new XMLDecoder(new ByteArrayInputStream(data));
        Object object = xmlDecoder.readObject();
        xmlDecoder.close();
        return clz.cast(object);
    }
}
