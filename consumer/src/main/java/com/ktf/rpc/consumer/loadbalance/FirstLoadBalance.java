package com.ktf.rpc.consumer.loadbalance;

import com.ktf.rpc.core.balancer.LoadBalance;
import com.ktf.rpc.core.common.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 自定义负载均衡策略
 * 取第一个
 */
@Slf4j
//@Component
public class FirstLoadBalance implements LoadBalance {

    /**
     * 自己定义负载均衡策略
     * @param services 从服务列表中自定义服务的选择方式
     * @return 返回该服务
     */
    @Override
    public ServiceInfo chooseOne(List<ServiceInfo> services) {
        log.info("---------FirstLoadBalance-----------------");
        return services.get(0);
    }

}
