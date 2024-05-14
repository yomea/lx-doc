package com.laxqnsys.core.aspect.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConcurrentLock {
	String key() default ""; 	// 格式 aaa:bb:${cc}:dd 系统替换${cc}
	int expire() default 60;  	// 默认设置redis过期时间
	
	// 抛出异常的名称，在 controllerAdvice中捕获不同异常返回不同值
	String exception() default "com.banksteel.opencms.commons.aspect.lock.AntiDuplicationException";
}
