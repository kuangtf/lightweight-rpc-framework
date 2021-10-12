package com.ktf.rpc.client.transport;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetClientTransportFactory {

    public static NetClientTransport getNetClientTransport(){
        return new NettyNetClientTransport();
    }


}
