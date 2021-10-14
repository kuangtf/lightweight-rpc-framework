package com.ktf.rpc.core.exception;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 3365624081242234230L;

    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String msg) {
        super(msg);
    }

    public ResourceNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ResourceNotFoundException(Throwable cause) {
        super(cause);
    }
}
