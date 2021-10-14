package com.ktf.rpc.core.register;

import com.ktf.rpc.core.common.ServiceInfo;

import java.io.IOException;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 服务注册
 */
public interface RegistryService {

    void register(ServiceInfo serviceInfo) throws Exception;

    void unRegister(ServiceInfo serviceInfo) throws Exception;

    void destroy() throws IOException;

}
