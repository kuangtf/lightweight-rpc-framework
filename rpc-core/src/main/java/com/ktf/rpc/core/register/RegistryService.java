package com.ktf.rpc.core.register;

import com.ktf.rpc.core.common.ServiceInfo;

import java.io.IOException;

/**
 * 服务注册发现
 */
public interface RegistryService {

    void register(ServiceInfo serviceInfo) throws Exception;

    void unRegister(ServiceInfo serviceInfo) throws Exception;

    void destroy() throws IOException;

}
