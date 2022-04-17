package com.ktf.rpc.server.processor;

import com.ktf.rpc.core.common.ServiceInfo;
import com.ktf.rpc.core.common.ServiceUtil;
import com.ktf.rpc.core.register.RegistryService;
import com.ktf.rpc.server.ThreadUtil.RpcThreadFactory;
import com.ktf.rpc.server.annotation.RpcService;
import com.ktf.rpc.server.config.RpcServerProperties;
import com.ktf.rpc.server.store.LocalServerCache;
import com.ktf.rpc.server.transport.RpcServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.CommandLineRunner;

import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * BeanPostProcessor接口的作用是在Spring完成实例化之后，对 Spring容器实例化的 Bean添加一些自定义的处理逻辑。
 */
@Slf4j
public class RpcServerProcessor implements BeanPostProcessor, CommandLineRunner {

    /**
     * 创建线程池
     */
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000), new RpcThreadFactory("RpcServerProcessor"));

    private final RegistryService registryService;

    private final RpcServerProperties properties;

    private final RpcServer rpcServer;

    public RpcServerProcessor(RegistryService registryService, RpcServer rpcServer, RpcServerProperties properties) {
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
                // 获取暴露服务的接口名称
                String interfaceName = rpcService.interfaceType().getName();
                // 获取服务版本
                String version = rpcService.version();
                // 将服务名称和对应的 bean（其实是被调用的服务实例）的映射缓存起来
                String serviceName = ServiceUtil.serviceKey(interfaceName, version);
                LocalServerCache.store(serviceName, bean);
                // 封装服务相关信息用于注册服务
                ServiceInfo serviceInfo = new ServiceInfo();
                // 设置服务名称：接口名 + 版本号
                serviceInfo.setServiceName(serviceName);
                // 服务启动端口
                serviceInfo.setPort(properties.getPort());
                // 设置服务地址
                serviceInfo.setAddress(InetAddress.getLocalHost().getHostAddress());
                // 设置服务版本
                serviceInfo.setVersion(version);

                log.info("version: {}", serviceInfo.getVersion());
                log.info("ServiceName: {}", serviceInfo.getServiceName());
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
        threadPoolExecutor.execute(() -> rpcServer.start(properties.getPort()));
        log.info("rpc server :{} start, port :{}", rpcServer, properties.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                // 关闭之后把服务从 ZK 上清除
                registryService.destroy();
            }catch (Exception e){
                log.error("zookeeper close exception", e);
            }
        }));
    }

}
