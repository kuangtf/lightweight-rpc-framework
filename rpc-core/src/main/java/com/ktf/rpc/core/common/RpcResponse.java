package com.ktf.rpc.core.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 * 封装的响应实体类
 */
@Data
public class RpcResponse implements Serializable {

    // 结果数据
    private Object data;

    // 附带消息，可能是异常之类的
    private String message;

}
