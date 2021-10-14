package com.ktf.rpc.core.common;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 格式话服务和版本的名称
 */
public class ServiceUtil {

    /**
     * 将服务名称和版本之间加一个 “-” 并返回
     */
    public static String serviceKey(String serviceName, String version) {
        return String.join("-", serviceName, version);
    }

}
