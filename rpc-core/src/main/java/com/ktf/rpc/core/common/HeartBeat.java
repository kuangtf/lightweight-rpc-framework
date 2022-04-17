package com.ktf.rpc.core.common;

import lombok.Data;

/**
 * @author github.com/kuangtf
 * @date 2022/2/20 21:29
 * 封装的心跳实体类
 */
@Data
public class HeartBeat {

    /**
     * 心跳请求的服务名 + 版本
     */
    private String serviceName;
    /**
     * 心跳请求调用的方法
     */
    private String method;

    /**
     *  心跳请求的参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     *  心跳请求的参数
     */
    private Object[] parameters;

}
