package com.ktf.rpc.server.config;

import com.ktf.rpc.core.register.RegistryService;
import com.ktf.rpc.core.register.ZookeeperRegistryService;
import com.ktf.rpc.server.RpcServerProvider;
import com.ktf.rpc.server.transport.NettyRpcServer;
import com.ktf.rpc.server.transport.RpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 */
@Configuration
@EnableConfigurationProperties(RpcServerProperties.class)
public class RpcServerAutoConfiguration {

    @Autowired
    private RpcServerProperties properties;

    /**
     * 创建一个服务注册实体到 IOC 容器中
     */
    @Bean
    @ConditionalOnMissingBean
    public RegistryService registryService() {
        return new ZookeeperRegistryService(properties.getRegistryAddr());
    }

    /**
     * 创建一个服务端的 Netty 传输类实体到 IOC 容器中
     */
    @Bean
    @ConditionalOnMissingBean(RpcServer.class)
    RpcServer RpcServer() {
        return new NettyRpcServer();
    }

    /**
     * 创建一个服务提供者实体到 IOC 容器中
     */
    @Bean
    @ConditionalOnMissingBean(RpcServerProvider.class)
    RpcServerProvider rpcServerProvider(@Autowired RegistryService registryService,
                                        @Autowired RpcServer rpcServer,
                                        @Autowired RpcServerProperties rpcServerProperties){
        return new RpcServerProvider(registryService, rpcServer, rpcServerProperties);
    }
}
