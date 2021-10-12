package com.ktf.rpc.provider.service;

import com.ktf.rpc.api.service.HelloWordService;
import com.ktf.rpc.server.annotation.RpcService;
import org.springframework.stereotype.Component;

@RpcService(interfaceType = HelloWordService.class)
public class HelloWordServiceImpl implements HelloWordService {

    @Override
    public String sayHello(String name) {
        return String.format("您好：%s, rpc 调用成功", name);
    }

}
