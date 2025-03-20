package com.laxqnsys.core.other.aspect.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wuzhenhong
 * @date 2024/5/14 8:48
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConcurrentLock {

    String key() default "";    // 格式 aaa:bb:${cc}:dd 系统替换${cc}
}
