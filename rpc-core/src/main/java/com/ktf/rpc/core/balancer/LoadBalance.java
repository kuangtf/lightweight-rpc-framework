package com.ktf.rpc.core.balancer;

import com.ktf.rpc.core.common.ServiceInfo;
import java.util.List;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 负载均衡算法接口
 */
public interface LoadBalance {

    /**
     * 选择一个服务
     * @param services 服务列表
     * @return 按照一定的规则选中的服务
     */
    ServiceInfo chooseOne(List<ServiceInfo> services);
}
