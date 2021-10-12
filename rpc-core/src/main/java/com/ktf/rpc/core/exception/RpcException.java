package com.ktf.rpc.core.exception;

public class RpcException extends RuntimeException {

    private static final long serialVersionUID = 3365624081242234230L;

    public RpcException() {
        super();
    }

    public RpcException(String msg) {
        super(msg);
    }

    public RpcException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }
}
