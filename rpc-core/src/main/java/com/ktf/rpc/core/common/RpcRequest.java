package com.ktf.rpc.core.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 封装的请求实体类
 */
@Data
public class RpcRequest implements Serializable {

    /**
     * 请求的服务名 + 版本
     */
    private String serviceName;
    /**
     * 请求调用的方法
     */
    private String method;

    /**
     *  参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     *  参数
     */
    private Object[] parameters;

}
