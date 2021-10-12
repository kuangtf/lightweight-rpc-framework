package com.ktf.rpc.core.discovery;

import com.ktf.rpc.core.common.ServiceInfo;

public interface DiscoveryService {

    /**
     *  发现
     */
    ServiceInfo discovery(String serviceName) throws Exception;

}
