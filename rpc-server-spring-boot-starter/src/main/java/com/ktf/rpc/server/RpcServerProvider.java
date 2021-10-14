package com.ktf.rpc.server;

import com.ktf.rpc.core.common.ServiceInfo;
import com.ktf.rpc.core.common.ServiceUtil;
import com.ktf.rpc.core.register.RegistryService;
import com.ktf.rpc.server.annotation.RpcService;
import com.ktf.rpc.server.config.RpcServerProperties;
import com.ktf.rpc.server.store.LocalServerCache;
import com.ktf.rpc.server.transport.RpcServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.CommandLineRunner;

import java.net.InetAddress;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * BeanPostProcessor接口的作用是在Spring完成实例化之后，对Spring容器实例化的Bean添加一些自定义的处理逻辑。
 */
@Slf4j
public class RpcServerProvider implements BeanPostProcessor, CommandLineRunner {

    @Autowired
    private final RegistryService registryService;

    @Autowired
    private final RpcServerProperties properties;

    @Autowired
    private final RpcServer rpcServer;

    public RpcServerProvider(RegistryService registryService, RpcServer rpcServer, RpcServerProperties properties) {
        this.registryService = registryService;
        this.properties = properties;
        this.rpcServer = rpcServer;
    }

    /**
     * 所有 bean 实例化之后添加自定义的处理逻辑
     * 1、暴露服务注册到注册中心
     * 2、容器启动后开启 netty 服务处理请求
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取 IOC 中的 RpcService 注解
        RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
        if (rpcService != null) {
            try {
                // 获取暴露服务的接口
                String serviceName = rpcService.interfaceType().getName();
                // 获取服务版本
                String version = rpcService.version();
                // 将服务名称和对应的 bean（其实是被调用的服务实例）的映射缓存起来
                LocalServerCache.store(ServiceUtil.serviceKey(serviceName, version), bean);
                // 封装服务相关信息用于注册服务
                ServiceInfo serviceInfo = new ServiceInfo();
                serviceInfo.setServiceName(ServiceUtil.serviceKey(serviceName, version));
                serviceInfo.setPort(properties.getPort());
                serviceInfo.setAddress(InetAddress.getLocalHost().getHostAddress());
                serviceInfo.setAppName(properties.getAppName());

                // 服务注册
                registryService.register(serviceInfo);
            } catch (Exception e) {
                log.error("服务注册出错:", e);
            }
        }
        return bean;
    }

    /**
     * 启动 rpc 服务, 处理请求
     *
     * addShutdownHook:
     *  jvm中增加一个关闭的钩子，当jvm关闭的时候，会执行系统中
     *  已经设置的所有通过方法addShutdownHook添加的钩子，当
     *  系统执行完这些钩子后，jvm才会关闭。所以这些钩子可以在
     *  jvm关闭的时候进行内存清理、对象销毁等操作。
     */
    @Override
    public void run(String... args) {
        new Thread(() -> rpcServer.start(properties.getPort())).start();
        log.info("rpc server :{} start, appName :{} , port :{}", rpcServer, properties.getAppName(), properties.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                // 关闭之后把服务从 ZK 上清除
                registryService.destroy();
            }catch (Exception e){
                log.error("ZK close exception", e);
            }
        }));
    }

}
