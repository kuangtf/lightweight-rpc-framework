package com.ktf.rpc.client.processor;

import com.ktf.rpc.client.annotation.RpcAutowired;
import com.ktf.rpc.client.config.RpcClientProperties;
import com.ktf.rpc.client.proxy.ClientStubProxyFactory;
import com.ktf.rpc.core.discovery.DiscoveryService;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

/**
 * @author github.com/kuangtf
 * @date 2021/10/13 21:35
 * BeanPostProcessor 接口的作用是在 Spring完成实例化之后，对 Spring容器实例化的 Bean添加一些自定义的处理逻辑
 */
public class RpcClientProcessor implements BeanPostProcessor {

    private final ClientStubProxyFactory clientStubProxyFactory;

    private final DiscoveryService discoveryService;

    private final RpcClientProperties properties;

    public RpcClientProcessor(ClientStubProxyFactory clientStubProxyFactory, DiscoveryService discoveryService, RpcClientProperties properties) {
        this.clientStubProxyFactory = clientStubProxyFactory;
        this.discoveryService = discoveryService;
        this.properties = properties;
    }

    /**
     * postProcessAfterInitialization 方法的返回值会被Spring容器作为处理后的Bean注册到容器中。
     * 如果你在postProcessAfterInitialization 方法中重新构造了一个Bean进行返回，而不是返回参数
     * 中的bean；那么你返回的Bean将会被注册到 Spring容器中。而原来在Spring中配置的Bean（被Spring
     * 实例化的Bean）将会被覆盖。
     * @param bean 刚刚由Spring容器调用过初始化方法（init-method）的Bean
     * @param beanName 在Spring配置元数据中Bean的名称（id or name）
     * @return 返回被自定义后了的 bean
     * @throws BeansException 抛出的异常
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取这个 bean 的 Class 对象，使用的是 AOP 工具类
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);
        // 使用 Spring 中的反射工具类中的 doWithFields 方法，在 clazz 类的所有字段上调用给定的回调函数，就是第二个参数
        ReflectionUtils.doWithFields(clazz, field -> {
            // 获取字段上的 RpcAutowired 注解，用于得到服务版本
            RpcAutowired rpcAutowired = AnnotationUtils.getAnnotation(field, RpcAutowired.class);
            if (rpcAutowired != null) {
                // 在访问时应该取消对该字段的检查
                field.setAccessible(true);
                // 动态修改被修饰字段的值为代理对象 ClientStubProxyFactory
                ReflectionUtils.setField(field, bean, clientStubProxyFactory.getProxy(field.getType(), rpcAutowired.version(), discoveryService, properties));
            }
        });
        return bean;
    }
}
