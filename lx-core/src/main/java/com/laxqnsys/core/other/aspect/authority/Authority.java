package com.laxqnsys.core.other.aspect.authority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限控制
 * @author wuzhenhong
 * @date 2024/5/16 16:23
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authority {

    /**
     * springEL参数表达式，用于获取方法上的参数
     *
     * @return
     */
    String[] spelArgs();

    /**
     * 权限校验实现的方法名
     *
     * @return
     */
    String methodName();

    /**
     * 注入到 beanFactory 的权限实现类
     *
     * @return
     */
    String beanName();
}
