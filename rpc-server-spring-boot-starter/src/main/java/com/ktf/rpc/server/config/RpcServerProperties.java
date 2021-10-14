package com.ktf.rpc.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 */
@Data
@ConfigurationProperties(prefix = "rpc.server")
public class RpcServerProperties {

    /**
     *  服务启动端口
     */
    private Integer port;

    /**
     *  服务名称
     */
    private String appName;

    /**
     *  注册中心地址
     */
    private String registryAddr = "127.0.0.1:2181";

}
