package com.ktf.rpc.client.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcAutowired {

    /**
     * 服务版本
     */
    String version() default "1.0";

}
