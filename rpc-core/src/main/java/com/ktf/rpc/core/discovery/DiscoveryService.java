package com.ktf.rpc.core.discovery;

import com.ktf.rpc.core.common.ServiceInfo;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 */
public interface DiscoveryService {

    /**
     * 服务发现
     * @param serviceName 服务名
     * @return 返回服务信息
     * @throws Exception 异常
     */
    ServiceInfo discovery(String serviceName) throws Exception;

}
