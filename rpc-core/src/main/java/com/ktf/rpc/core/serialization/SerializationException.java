package com.ktf.rpc.core.serialization;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 序列化异常处理
 */
public class SerializationException extends RuntimeException {

    private static final long serialVersionUID = 3365624081242234230L;

    public SerializationException() {
        super();
    }

    public SerializationException(String msg) {
        super(msg);
    }

    public SerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }
}
