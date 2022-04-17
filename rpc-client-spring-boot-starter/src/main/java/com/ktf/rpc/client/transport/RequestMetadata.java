package com.ktf.rpc.client.transport;

import com.ktf.rpc.core.common.RpcRequest;
import com.ktf.rpc.core.protocol.MessageProtocol;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 请求元数据
 */
@Data
@Builder
public class RequestMetadata implements Serializable {

    /**
     *  请求格式
     */
    private MessageProtocol<RpcRequest> protocol;

    /**
     *  请求服务的地址
     */
    private String address;

    /**
     *  请求服务的端口
     */
    private Integer port;

    /**
     *  服务调用超时时间
     */
    private Integer timeout;

}
