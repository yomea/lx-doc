package com.laxqnsys.core.aspect.authority;

import com.laxqnsys.core.context.LoginContext;
import com.laxqnsys.core.util.spel.SpringElUtil;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * @author wuzhenhong
 * @date 2024/5/16 16:23
 */
@Slf4j
@Aspect
@Component
public class AuthorityAspect implements ApplicationContextAware {

    private static final Object[] EMPTY_ARRAY = new Object[0];

    private ApplicationContext applicationContext;

    /**
     * {@link Authority}
     */
    @Pointcut("@annotation(com.laxqnsys.core.aspect.authority.Authority) || @within(com.laxqnsys.core.aspect.authority.Authority)")
    public void pointCut() {
        // 仅仅是为了设置切点
    }

    @Before("pointCut()")
    public Object before(JoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] parameters = joinPoint.getArgs();
        String[] parameterNames = methodSignature.getParameterNames();
        Authority authority = method.getAnnotation(Authority.class);

        String[] spelArgs = authority.spelArgs();
        Object[] finalArgs = EMPTY_ARRAY;
        if (Objects.nonNull(spelArgs) && spelArgs.length > 0) {
            Map<String, Object> context = new HashMap<>();
            for (int i = 0; i < parameters.length; i++) {
                context.put(parameterNames[i], parameters[i]);
            }
            context.put("userInfoBO", LoginContext.getUserInfo());
            finalArgs = Arrays.stream(spelArgs).map(args -> SpringElUtil.evaluate(args, context))
                .toArray();
        }

        String beanName = authority.beanName();
        Object bean = this.applicationContext.getBean(beanName);
        Class<?>[] classes = (Class<?>[]) Arrays.stream(finalArgs).map(Object::getClass).toArray();
        Method method1 = ReflectionUtils.findMethod(bean.getClass(), authority.methodName(), classes);
        return method1.invoke(bean, finalArgs);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
