package com.ktf.rpc.consumer.controller;

import com.ktf.rpc.api.service.HelloWordService;
import com.ktf.rpc.client.annotation.RpcAutowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 */
@Controller
public class HelloWorldController {

    @RpcAutowired(version = "2.0")
    HelloWordService helloWordService;

    @GetMapping("/hello/world")
    @ResponseBody
    public String helloWordService(@RequestParam("name") String name){
        return  helloWordService.sayHello(name);
    }

}
