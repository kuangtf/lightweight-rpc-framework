package com.ktf.rpc.client.annotation;

import java.lang.annotation.*;

/**
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcAutowired {

    /**
     * 服务版本
     */
    String version() default "1.0";

}
