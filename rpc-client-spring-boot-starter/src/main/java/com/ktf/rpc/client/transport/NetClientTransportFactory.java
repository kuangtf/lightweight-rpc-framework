package com.ktf.rpc.client.transport;

import lombok.extern.slf4j.Slf4j;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 客户端传输类工厂
 */
@Slf4j
public class NetClientTransportFactory {

    public static NetClientTransport getNetClientTransport(){
        return new NettyNetClientTransport();
    }

}
