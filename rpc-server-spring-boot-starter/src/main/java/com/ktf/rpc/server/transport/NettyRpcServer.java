package com.ktf.rpc.server.transport;

import com.ktf.rpc.core.codec.RpcDecoder;
import com.ktf.rpc.core.codec.RpcEncoder;
import com.ktf.rpc.server.handler.RpcRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;


@Slf4j
public class NettyRpcServer implements RpcServer {

    @Override
    public void start(int port) {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            String serverAddress = InetAddress.getLocalHost().getHostAddress();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    // 协议编码
                                    .addLast(new RpcEncoder())
                                    // 协议解码
                                    .addLast(new RpcDecoder())
                                    // 请求处理器
                                    .addLast(new RpcRequestHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.bind(serverAddress, port).sync();
            log.info("server addr {} started on port {}", serverAddress, port);
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
