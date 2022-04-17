package com.ktf.rpc.client.config;

import com.ktf.rpc.client.proxy.ClientStubProxyFactory;
import com.ktf.rpc.client.processor.RpcClientProcessor;
import com.ktf.rpc.core.balancer.FullRoundBalance;
import com.ktf.rpc.core.balancer.LoadBalance;
import com.ktf.rpc.core.balancer.RandomBalance;
import com.ktf.rpc.core.discovery.DiscoveryService;
import com.ktf.rpc.core.discovery.ZookeeperDiscoveryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author github.com/kuangtf
 * @date 2021/10/13 21:35
 *
 * EnableConfigurationProperties 这个注解用于将带有 ConfigurationProperties 的类注入 IOC 中
 */
@Configuration
@EnableConfigurationProperties(RpcClientProperties.class)
public class RpcClientAutoConfiguration {

    @Autowired
    private RpcClientProperties rpcClientProperties;

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
     * Primary：该注解表示当IOC中有多个实现相同接口的bean时，优先选择有这个注解的bean注入（默认）
     * ConditionalOnProperty：可以通过配置文件中的属性值来判定 configuration 是否被注入
     * ConditionalOnMissingBean：判断当前需要注入 IOC容器中的bean的实现类是否已经含有，有的话不注入，没有就注入
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
     * ConditionalOnBean 判断当前需要注册的bean的实现类否被spring管理，如果被管理则注入，反之不注入
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({LoadBalance.class})
    public DiscoveryService discoveryService(@Autowired LoadBalance loadBalance) {
        return new ZookeeperDiscoveryServiceImpl(rpcClientProperties.getDiscoveryAddr(), loadBalance);
    }

    /**
     * 创建 RpcClientProcessor bean
     */
    @Bean
    @ConditionalOnMissingBean
    public RpcClientProcessor rpcClientProcessor(@Autowired ClientStubProxyFactory clientStubProxyFactory,
                                                 @Autowired DiscoveryService discoveryService) {
        return new RpcClientProcessor(clientStubProxyFactory, discoveryService, rpcClientProperties);
    }

}
