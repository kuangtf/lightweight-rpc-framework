package com.ktf.rpc.client.transport;

import com.ktf.rpc.core.common.RpcResponse;
import com.ktf.rpc.core.protocol.MessageProtocol;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 网络传输层
 */
public interface NetClientTransport {

    /**
     *  发送数据
     */
    MessageProtocol<RpcResponse> sendRequest(RequestMetadata metadata) throws Exception;

}
