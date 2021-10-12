package com.ktf.rpc.consumer.loadbalance;

import com.ktf.rpc.core.balancer.LoadBalance;
import com.ktf.rpc.core.common.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义负载均衡策略
 * 取第一个
 */
@Slf4j
@Component
public class FirstLoadBalance implements LoadBalance {

    @Override
    public ServiceInfo chooseOne(List<ServiceInfo> services) {
        log.info("---------FirstLoadBalance-----------------");
        return services.get(0);
    }
}
