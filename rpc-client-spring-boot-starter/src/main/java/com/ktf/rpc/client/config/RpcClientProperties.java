package com.ktf.rpc.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author github.com/kuangtf
 * @date 2021/10/13 21:35
 */
@Data
public class RpcClientProperties {

    /**
     *  负载均衡
     */
    private String balance;

    /**
     *  序列化
     */
    private String serialization;

    /**
     *  服务发现地址
     */
    private String discoveryAddr = "127.0.0.1:2181";

    /**
     *  服务调用超时
     */
    private Integer timeout;

}
