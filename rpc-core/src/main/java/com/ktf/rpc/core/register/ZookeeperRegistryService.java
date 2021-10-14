package com.ktf.rpc.core.register;

import com.ktf.rpc.core.common.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.io.IOException;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 服务注册相关
 */
@Slf4j
public class ZookeeperRegistryService implements RegistryService {

    // 初始 sleep 时间，单位是毫秒
    public static final int BASE_SLEEP_TIME_MS = 1000;
    // 最大重试次数
    public static final int MAX_RETRIES = 3;
    // 基础节点
    public static final String ZK_BASE_PATH = "/ktf_rpc";

    private ServiceDiscovery<ServiceInfo> serviceDiscovery;

    /**
     * 初始化的一些东西
     */
    public ZookeeperRegistryService(String registryAddr) {
        try {
            // registryAddr：连接字符串，也就是 ZK 的地址
            // ExponentialBackoffRetry：重试连接策略
            // 创建 ZK 客户端
            CuratorFramework client = CuratorFrameworkFactory.newClient(registryAddr, new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
            // 开启连接
            client.start();
            JsonInstanceSerializer<ServiceInfo> serializer = new JsonInstanceSerializer<>(ServiceInfo.class);
            this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceInfo.class)
                    .client(client)
                    .serializer(serializer)
                    .basePath(ZK_BASE_PATH)
                    .build();
            this.serviceDiscovery.start();
        } catch (Exception e) {
            log.error("serviceDiscovery start error : ", e);
        }
    }

    /**
     * 服务注册
     */
    @Override
    public void register(ServiceInfo serviceInfo) throws Exception {
        // 设置服务实例
        ServiceInstance<ServiceInfo> serviceInstance = ServiceInstance
                .<ServiceInfo>builder()
                .name(serviceInfo.getServiceName())
                .address(serviceInfo.getAddress())
                .port(serviceInfo.getPort())
                .payload(serviceInfo)
                .build();
        // 注册服务
        serviceDiscovery.registerService(serviceInstance);
    }

    /**
     * 注销服务（目前没有使用到）
     */
    @Override
    public void unRegister(ServiceInfo serviceInfo) throws Exception {
        ServiceInstance<ServiceInfo> serviceInstance = ServiceInstance
                .<ServiceInfo>builder()
                .name(serviceInfo.getServiceName())
                .address(serviceInfo.getAddress())
                .port(serviceInfo.getPort())
                .payload(serviceInfo)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
    }

    /**
     * 关闭服务
     */
    @Override
    public void destroy() throws IOException {
        serviceDiscovery.close();
    }

}
