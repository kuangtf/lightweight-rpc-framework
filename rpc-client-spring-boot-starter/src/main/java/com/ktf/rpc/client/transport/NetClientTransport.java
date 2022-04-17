package com.ktf.rpc.client.transport;

import com.ktf.rpc.core.common.RpcResponse;
import com.ktf.rpc.core.protocol.MessageProtocol;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 客户端传输类接口
 */
public interface NetClientTransport {

    /**
     * 发送请求
     * @param metadata 请求元数据
     * @return 响应结果
     * @throws Exception 异常
     */
    MessageProtocol<RpcResponse> sendRequest(RequestMetadata metadata) throws Exception;

}
