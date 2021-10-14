package com.ktf.rpc.client.handler;

import com.ktf.rpc.client.cache.LocalRpcResponseCache;
import com.ktf.rpc.core.common.RpcResponse;
import com.ktf.rpc.core.protocol.MessageProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author github.com/kuangtf
 * @date 2021/10/13 21:35
 * 数据响应处理器
 */
@Slf4j
public class RpcResponseHandler extends SimpleChannelInboundHandler<MessageProtocol<RpcResponse>> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol<RpcResponse> rpcResponseMessageProtocol) throws Exception {
        // 获取请求头 id
        String requestId = rpcResponseMessageProtocol.getHeader().getRequestId();
        // 收到响应，设置响应数据
        LocalRpcResponseCache.fillResponse(requestId, rpcResponseMessageProtocol);
    }
}
