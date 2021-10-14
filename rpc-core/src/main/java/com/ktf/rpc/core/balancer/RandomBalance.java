package com.ktf.rpc.core.balancer;

import com.ktf.rpc.core.common.ServiceInfo;

import java.util.List;
import java.util.Random;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 随机算法
 */
public class RandomBalance implements LoadBalance{

    private static final Random random = new Random();

    @Override
    public ServiceInfo chooseOne(List<ServiceInfo> services) {
        return services.get(random.nextInt(services.size()));
    }
}
