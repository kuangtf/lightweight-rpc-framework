package com.ktf.rpc.client.proxy;

import com.ktf.rpc.client.config.RpcClientProperties;
import com.ktf.rpc.core.discovery.DiscoveryService;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 */
public class ClientStubProxyFactory {

    private final Map<Class<?>, Object> objectCache = new HashMap<>();

    /**
     * 获取代理对象（使用 JDK 的代理机制）
     * @param clazz   接口
     * @param version 服务版本
     * @return 代理对象
     */
    @SuppressWarnings ("unchecked")
    public <T> T getProxy(Class<T> clazz, String version, DiscoveryService discoveryService, RpcClientProperties properties) {
        // computeIfAbsent JDK8 中新增的方法，相当于构建 java 本地缓存
        return (T) objectCache.computeIfAbsent(clazz, clz ->
                Proxy.newProxyInstance(clz.getClassLoader(), new Class[]{clz},
                        new ClientStubInvocationHandler(discoveryService, properties, clz, version))
        );
    }
}
