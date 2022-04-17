package com.ktf.rpc.core.exception;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 */
public class RpcException extends RuntimeException {

    private static final long serialVersionUID = 3365624081242234230L;

    public RpcException(String msg) {
        super(msg);
    }
}
