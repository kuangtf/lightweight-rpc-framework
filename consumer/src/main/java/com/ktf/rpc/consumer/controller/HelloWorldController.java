package com.ktf.rpc.consumer.controller;

import com.ktf.rpc.api.service.HelloWordService;
import com.ktf.rpc.client.annotation.RpcAutowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWorldController {

    @RpcAutowired(version = "2.0")
    private HelloWordService helloWordService;

    @GetMapping("/hello")
    @ResponseBody
    public String pullServiceInfo1(@RequestParam("name") String name){
        return  helloWordService.sayHello(name);
    }

}
