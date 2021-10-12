package com.ktf.rpc.core.balancer;

import com.ktf.rpc.core.common.ServiceInfo;
import java.util.List;

/**
 * 负载均衡算法接口
 */
public interface LoadBalance {
    /**
     *
     * @param services
     * @return
     */
    ServiceInfo chooseOne(List<ServiceInfo> services);
}
