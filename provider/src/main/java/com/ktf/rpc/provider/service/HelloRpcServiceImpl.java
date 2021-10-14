package com.ktf.rpc.provider.service;

import com.ktf.rpc.api.service.HelloRpcService;
import com.ktf.rpc.api.service.HelloWordService;
import com.ktf.rpc.server.annotation.RpcService;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 */
@RpcService(interfaceType = HelloRpcService.class)
public class HelloRpcServiceImpl implements HelloRpcService {

    @Override
    public String sayHello(String name) {
        return String.format("您好：%s, HelloRpcService 1.0 调用成功", name);
    }

}
