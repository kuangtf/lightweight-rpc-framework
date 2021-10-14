package com.ktf.rpc.core.discovery;

import com.ktf.rpc.core.balancer.LoadBalance;
import com.ktf.rpc.core.common.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 服务发现
 */
@Slf4j
public class ZookeeperDiscoveryService implements DiscoveryService {

    // 初始 sleep 时间，但是是毫秒
    public static final int BASE_SLEEP_TIME_MS = 1000;
    // 最大重试次数
    public static final int MAX_RETRIES = 3;
    // 基础节点
    public static final String ZK_BASE_PATH = "/ktf_rpc";

    private ServiceDiscovery<ServiceInfo> serviceDiscovery;

    private final LoadBalance loadBalance;

    public ZookeeperDiscoveryService(String registryAddr, LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
        log.info("the balance is {}", loadBalance.toString());
        try {
            CuratorFramework client = CuratorFrameworkFactory.newClient(registryAddr, new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
            client.start();
            JsonInstanceSerializer<ServiceInfo> serializer = new JsonInstanceSerializer<>(ServiceInfo.class);
            this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceInfo.class)
                    .client(client)
                    .serializer(serializer)
                    .basePath(ZK_BASE_PATH)
                    .build();
            this.serviceDiscovery.start();
        } catch (Exception e) {
            log.error("serviceDiscovery start error :", e);
        }
    }

    /**
     *  服务发现
     */
    @Override
    public ServiceInfo discovery(String serviceName) throws Exception {
        // 根据服务名去 ZK 中查询服务实例
        Collection<ServiceInstance<ServiceInfo>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        // 返回服务信息实体
        return CollectionUtils.isEmpty(serviceInstances) ? null
                : loadBalance.chooseOne(serviceInstances.stream()
                .map(ServiceInstance::getPayload).collect(Collectors.toList()));
    }

}
