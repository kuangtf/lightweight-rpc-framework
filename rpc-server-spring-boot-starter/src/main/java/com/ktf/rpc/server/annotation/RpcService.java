package com.ktf.rpc.server.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface RpcService {

    /**
     *  暴露服务接口类型
     */
    Class<?> interfaceType() default Object.class;

    /**
     *  服务版本
     */
    String version() default "1.0";
}
