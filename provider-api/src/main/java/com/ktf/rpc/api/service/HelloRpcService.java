package com.ktf.rpc.api.service;

/**
 * @author github.com/kuangtf
 * @date 2021/10/13 21:35
 */
public interface HelloRpcService {

    /**
     * 测试方法
     * @param name 名字
     * @return 返回结果
     */
    String sayHello(String name);
}
