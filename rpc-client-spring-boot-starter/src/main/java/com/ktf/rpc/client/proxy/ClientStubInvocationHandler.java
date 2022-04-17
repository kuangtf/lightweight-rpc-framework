package com.ktf.rpc.client.proxy;

import com.ktf.rpc.client.config.RpcClientProperties;
import com.ktf.rpc.client.transport.NetClientTransportFactory;
import com.ktf.rpc.client.transport.RequestMetadata;
import com.ktf.rpc.core.common.*;
import com.ktf.rpc.core.discovery.DiscoveryService;
import com.ktf.rpc.core.exception.ResourceNotFoundException;
import com.ktf.rpc.core.exception.RpcException;
import com.ktf.rpc.core.protocol.MessageHeader;
import com.ktf.rpc.core.protocol.MessageProtocol;
import com.ktf.rpc.core.protocol.MsgStatus;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 服务端代理类，使用的是 JDK 动态代理
 */
@Slf4j
public class ClientStubInvocationHandler implements InvocationHandler {

    private final DiscoveryService discoveryService;

    private final RpcClientProperties properties;

    private final Class<?> calzz;

    private final String version;

    public ClientStubInvocationHandler(DiscoveryService discoveryService, RpcClientProperties properties, Class<?> calzz, String version) {
        super();
        this.calzz = calzz;
        this.version = version;
        this.discoveryService = discoveryService;
        this.properties = properties;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 1、获得服务信息
        ServiceInfo serviceInfo = discoveryService.discovery(ServiceUtil.serviceKey(this.calzz.getName(), this.version));

        if (serviceInfo == null) {
            log.error("service is not exit");
            throw new ResourceNotFoundException("404");
        }

        // 封装请求协议
        MessageProtocol<RpcRequest> messageProtocol = new MessageProtocol<>();
        // 设置请求体
        RpcRequest request = new RpcRequest();
        request.setServiceName(serviceInfo.getServiceName());
        request.setMethod(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        messageProtocol.setBody(request);
        // 设置请求头
        messageProtocol.setHeader(MessageHeader.build(properties.getSerialization(), request.toString().getBytes().length));
        log.info(messageProtocol.toString());

        // 发送网络请求，拿到结果
        MessageProtocol<RpcResponse> responseMessageProtocol = NetClientTransportFactory.getNetClientTransport()
                .sendRequest(RequestMetadata.builder()
                        .protocol(messageProtocol)
                        .address(serviceInfo.getAddress())
                        .port(serviceInfo.getPort())
                        .timeout(properties.getTimeout()).build());

        if (responseMessageProtocol == null) {
            log.error("请求超时");
            throw new RpcException("rpc调用结果失败， 请求超时 timeout:" + properties.getTimeout());
        }

        // 从协议头中获取响应状态并比较，0：成功  1：失败
        if (!MsgStatus.isSuccess(responseMessageProtocol.getHeader().getStatus())) {
            log.error("rpc调用结果失败， message：{}", responseMessageProtocol.getBody().getMessage());
            throw new RpcException(responseMessageProtocol.getBody().getMessage());
        }

        return responseMessageProtocol.getBody().getData();
    }
}
