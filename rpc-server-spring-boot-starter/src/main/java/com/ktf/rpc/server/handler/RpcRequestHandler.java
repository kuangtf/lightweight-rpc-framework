package com.ktf.rpc.server.handler;

import com.ktf.rpc.core.common.RpcRequest;
import com.ktf.rpc.core.common.RpcResponse;
import com.ktf.rpc.core.protocol.MessageHeader;
import com.ktf.rpc.core.protocol.MessageProtocol;
import com.ktf.rpc.core.protocol.MsgStatus;
import com.ktf.rpc.core.protocol.MsgType;
import com.ktf.rpc.server.store.LocalServerCache;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 服务端处理请求 handler
 */
@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<MessageProtocol<RpcRequest>> {

    // 创建线程池
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol<RpcRequest> rpcRequestMessageProtocol) throws Exception {
        // 多线程处理每个请求
        threadPoolExecutor.submit(() -> {
            // 创建响应对象
            MessageProtocol<RpcResponse> resProtocol = new MessageProtocol<>();
            RpcResponse response = new RpcResponse();
            // 获取请求头信息
            MessageHeader header = rpcRequestMessageProtocol.getHeader();
            // 设置头部消息类型为响应
            header.setMsgType(MsgType.RESPONSE.getType());
            try {
                // 处理请求体并封处理结果
                Object result = handle(rpcRequestMessageProtocol.getBody());
                response.setData(result);
                // 设置传输协议的头为 success，并封装传输协议
                header.setStatus(MsgStatus.SUCCESS.getCode());
                resProtocol.setHeader(header);
                resProtocol.setBody(response);
            } catch (Throwable throwable) {
                header.setStatus(MsgStatus.FAIL.getCode());
                response.setMessage(throwable.toString());
                log.error("process request {} error", header.getRequestId(), throwable);
            }
            // 把封装好的响应返回给调用方
            channelHandlerContext.writeAndFlush(resProtocol);
        });

    }

    /**
     * 使用反射调用服务方法
     */
    private Object handle(RpcRequest request) {
        try {
            // 从缓存中获取服务对象，请求中的服务名称是经过格式化之后的，与缓存中的格式匹配，不需要再转换
            Object bean = LocalServerCache.get(request.getServiceName());
            if (bean == null) {
                throw new RuntimeException(String.format("service not exist: %s !", request.getServiceName()));
            }
            // 反射调用，核心代码
            Method method = bean.getClass().getMethod(request.getMethod(), request.getParameterTypes());
            return method.invoke(bean, request.getParameters());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
