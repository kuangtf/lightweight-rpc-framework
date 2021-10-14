package com.ktf.rpc.server.store;

import java.util.HashMap;
import java.util.Map;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 在处理 RPC 请求时可以直接通过 cache 拿到对应的服务进行调用，避免反射实例化服务开销
 */
public final class LocalServerCache {

    private static final Map<String, Object> serverCacheMap = new HashMap<>();

    /**
     * merge 方法，会先判断指定的 key 是否存在，如果不存在，则添加键值对到 serverCacheMap 中。
     * 第三个参数为重新映射函数，用于重新计算值，这里是一个 lambda 表达式，
     */
    public static void store(String serverName, Object server) {
        serverCacheMap.merge(serverName, server, (Object oldObj, Object newObj) -> newObj);
    }

    /**
     * 获取服务名称对应的服务实例
     */
    public static Object get(String serverName) {
        return serverCacheMap.get(serverName);
    }

}
