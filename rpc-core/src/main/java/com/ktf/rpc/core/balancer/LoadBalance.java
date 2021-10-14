package com.ktf.rpc.core.balancer;

import com.ktf.rpc.core.common.ServiceInfo;
import java.util.List;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 负载均衡算法接口
 */
public interface LoadBalance {

    ServiceInfo chooseOne(List<ServiceInfo> services);
}
