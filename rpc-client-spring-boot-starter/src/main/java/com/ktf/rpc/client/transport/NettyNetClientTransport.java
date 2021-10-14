package com.ktf.rpc.client.transport;

import com.ktf.rpc.client.cache.LocalRpcResponseCache;
import com.ktf.rpc.client.handler.RpcResponseHandler;
import com.ktf.rpc.core.codec.RpcDecoder;
import com.ktf.rpc.core.codec.RpcEncoder;
import com.ktf.rpc.core.common.RpcRequest;
import com.ktf.rpc.core.common.RpcResponse;
import com.ktf.rpc.core.protocol.MessageProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 客户端传输层
 */
@Slf4j
public class NettyNetClientTransport implements NetClientTransport {

    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private final RpcResponseHandler handler;

    public NettyNetClientTransport() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        handler = new RpcResponseHandler();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                // 解码 是入站操作 将二进制解码成消息
                                .addLast(new RpcDecoder())
                                // 接收响应 入站操作
                                .addLast(handler)
                                // 编码 是出站操作 将消息编写二进制
                                .addLast(new RpcEncoder<>());
                    }
                });
    }

    @Override
    public MessageProtocol<RpcResponse> sendRequest(RequestMetadata metadata) throws Exception {
        // 请求的消息协议类型
        MessageProtocol<RpcRequest> protocol = metadata.getProtocol();
        // 异步返回结果
        RpcFuture<MessageProtocol<RpcResponse>> future = new RpcFuture<>();
        // 将请求 id 和对应的异步返回结果缓存起来
        LocalRpcResponseCache.add(protocol.getHeader().getRequestId(), future);

        // 使用 netty 连接到服务端
        ChannelFuture channelFuture = bootstrap.connect(metadata.getAddress(), metadata.getPort()).sync();
        // 添加回调函数，在连接之后执行
        channelFuture.addListener((ChannelFutureListener) arg0 -> {
            if (channelFuture.isSuccess()) {
                log.info("connect rpc server {} on port {} success.", metadata.getAddress(), metadata.getPort());
            } else {
                log.error("connect rpc server {} on port {} failed.", metadata.getAddress(), metadata.getPort());
                channelFuture.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
            }
        });
        // 将数据发送给服务端
        channelFuture.channel().writeAndFlush(protocol);
        // 异步获取响应
        return metadata.getTimeout() != null ? future.get(metadata.getTimeout(), TimeUnit.MILLISECONDS) : future.get();
    }
}
