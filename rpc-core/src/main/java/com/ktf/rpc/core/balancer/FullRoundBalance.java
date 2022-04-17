package com.ktf.rpc.core.balancer;

import com.ktf.rpc.core.common.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 轮询算法
 */
public class FullRoundBalance implements LoadBalance {

    private int index;

    @Override
    public synchronized ServiceInfo chooseOne(List<ServiceInfo> services) {
        // 加锁防止多线程情况下，index 超出 services.size()
        if (index >= services.size()) {
            index = 0;
        }
        return services.get(index ++);
    }
}
