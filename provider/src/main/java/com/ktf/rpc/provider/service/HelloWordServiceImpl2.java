package com.ktf.rpc.provider.service;

import com.ktf.rpc.api.service.HelloWordService;
import com.ktf.rpc.server.annotation.RpcService;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 */
@RpcService(interfaceType = HelloWordService.class, version = "2.0")
public class HelloWordServiceImpl2 implements HelloWordService {

    @Override
    public String sayHello(String name) {
        return String.format("您好：%s, HelloWorldService 2.0 调用成功", name);
    }

}
