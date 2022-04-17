package com.ktf.rpc.server.config;

import com.ktf.rpc.core.register.RegistryService;
import com.ktf.rpc.core.register.ZookeeperRegistryServiceImpl;
import com.ktf.rpc.server.processor.RpcServerProcessor;
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
     * 创建服务注册 bean
     */
    @Bean
    @ConditionalOnMissingBean
    public RegistryService registryService() {
        return new ZookeeperRegistryServiceImpl(properties.getRegistryAddr());
    }

    /**
     * 创建 NettyRpcServer bean
     */
    @Bean
    @ConditionalOnMissingBean
    RpcServer rpcServer() {
        return new NettyRpcServer();
    }

    /**
     * 创建 RpcServerProvider bean
     */
    @Bean
    @ConditionalOnMissingBean
    RpcServerProcessor rpcServerProvider(@Autowired RegistryService registryService,
                                         @Autowired RpcServer rpcServer,
                                         @Autowired RpcServerProperties rpcServerProperties){
        return new RpcServerProcessor(registryService, rpcServer, rpcServerProperties);
    }
}
