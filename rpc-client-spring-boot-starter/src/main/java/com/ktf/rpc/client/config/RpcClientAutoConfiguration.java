package com.ktf.rpc.client.config;

import com.ktf.rpc.client.proxy.ClientStubProxyFactory;
import com.ktf.rpc.client.processor.RpcClientProcessor;
import com.ktf.rpc.core.balancer.FullRoundBalance;
import com.ktf.rpc.core.balancer.LoadBalance;
import com.ktf.rpc.core.balancer.RandomBalance;
import com.ktf.rpc.core.discovery.DiscoveryService;
import com.ktf.rpc.core.discovery.ZookeeperDiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author github.com/kuangtf
 * @date 2021/10/13 21:35
 */
@Configuration
public class RpcClientAutoConfiguration {

    /**
     * 创建客户端配置 bean
     */
    @Bean
    @ConfigurationProperties(prefix = "rpc.client")
    public RpcClientProperties ppcClientProperties() {
        return new RpcClientProperties();
    }

    /**
     * 创建客户端代理 bean
     */
    @Bean
    @ConditionalOnMissingBean
    public ClientStubProxyFactory clientStubProxyFactory() {
        return new ClientStubProxyFactory();
    }

    /**
     * 创建随机负载均衡 bean，这是默认的负载均衡策略
     */
    @Primary
    @Bean(name = "loadBalance")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "balance", havingValue = "randomBalance", matchIfMissing = true)
    public LoadBalance randomBalance() {
        return new RandomBalance();
    }

    /**
     * 创建轮询 bean
     */
    @Bean(name = "loadBalance")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "balance", havingValue = "fullRoundBalance")
    public LoadBalance loadBalance() {
        return new FullRoundBalance();
    }

    /**
     * 创建服务发现 bean
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({RpcClientProperties.class, LoadBalance.class})
    public DiscoveryService discoveryService(@Autowired RpcClientProperties properties,
                                             @Autowired LoadBalance loadBalance) {
        return new ZookeeperDiscoveryService(properties.getDiscoveryAddr(), loadBalance);
    }


    @Bean
    @ConditionalOnMissingBean
    public RpcClientProcessor rpcClientProcessor(@Autowired ClientStubProxyFactory clientStubProxyFactory,
                                                 @Autowired DiscoveryService discoveryService,
                                                 @Autowired RpcClientProperties properties) {
        return new RpcClientProcessor(clientStubProxyFactory, discoveryService, properties);
    }

}
