package com.ktf.rpc.provider.service;

import com.ktf.rpc.api.service.HelloWordService;
import com.ktf.rpc.server.annotation.RpcService;
import org.springframework.stereotype.Component;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 */
@RpcService(interfaceType = HelloWordService.class)
public class HelloWordServiceImpl implements HelloWordService {

    @Override
    public String sayHello(String name) {
        return String.format("您好：%s, HelloWorldService 1.0 调用成功", name);
    }

}
