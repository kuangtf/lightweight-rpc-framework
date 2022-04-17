package com.ktf.rpc.core.register;

import com.ktf.rpc.core.common.ServiceInfo;

import java.io.IOException;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 服务注册
 */
public interface RegistryService {

    /**
     * 服务注册
     * @param serviceInfo 服务相关信息
     * @throws Exception 异常
     */
    void register(ServiceInfo serviceInfo) throws Exception;

    /**
     * 服务注销
     * @param serviceInfo 服务相关信息
     * @throws Exception 异常
     */
    void unRegister(ServiceInfo serviceInfo) throws Exception;

    /**
     * 服务关闭
     * @throws IOException 异常
     */
    void destroy() throws IOException;

    /**
     * 更新服务
     * @param serviceInfo 服务相关信息
     * @throws Exception 异常
     */
    void update(ServiceInfo serviceInfo) throws Exception;

}
