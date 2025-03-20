package com.laxqnsys.core.other.aspect.lock;

import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.other.context.LoginContext;
import com.laxqnsys.core.other.util.ongl.OnglUtils;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.ognl.OgnlException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author wuzhenhong
 * @date 2024/5/14 8:48
 */
@Slf4j
@Aspect
@Component
public class ConcurrentLockAspect {

    private Object OBJECT = new Object();

    private Map<String, Object> LOCK = new ConcurrentHashMap<>();

    /**
     * {@link ConcurrentLock}
     */
    @Pointcut("@annotation(com.laxqnsys.core.other.aspect.lock.ConcurrentLock))")
    public void pointCut() {
        // 仅仅是为了设置切点
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] parameters = joinPoint.getArgs();
        String[] parameterNames = methodSignature.getParameterNames();
        ConcurrentLock lock = method.getAnnotation(ConcurrentLock.class);

        if (lock != null && org.springframework.util.StringUtils.hasText(lock.key())) {
            String key;
            try {
                key = this.getKey(lock, parameterNames, parameters);
            } catch (Exception e) {
                log.error("获取redis key 失败: method={}, key={}", method.getName(), lock.key(), e);
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "获取锁key失败");
            }
            while (LOCK.putIfAbsent(key, OBJECT) != null) {
                Thread.yield();
            }
            try {
                return joinPoint.proceed();
            } catch (BusinessException e) {
                throw e;
            } catch (Throwable e) {
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "调用失败！", e);
            } finally {
                LOCK.remove(key);
            }
        } else {
            return joinPoint.proceed();
        }
    }

    private String getKey(ConcurrentLock lock, String[] parameterNames, Object[] parameters) throws OgnlException {
        Map<String, Object> context = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            // 以后根据需要加过滤， request和multipartFile类型的数据不可能作为redis key 主键内容
            if (!(parameters[i] instanceof HttpServletRequest || parameters[i] instanceof MultipartFile)) {
                context.put(parameterNames[i], parameters[i]);
            }
        }
        context.put("userInfoBO", LoginContext.getUserInfo());
        String key = lock.key();
        return OnglUtils.evaluate(key, context);
    }
}
