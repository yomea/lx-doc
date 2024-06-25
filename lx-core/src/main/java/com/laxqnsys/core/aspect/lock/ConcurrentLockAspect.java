package com.laxqnsys.core.aspect.lock;

import cn.hutool.json.JSONUtil;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.context.LoginContext;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.ognl.Ognl;
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

    // 非贪吃模式匹配
    private Pattern pattern = Pattern.compile("(\\$\\{)([\\w\\W]+?)(\\})");
    /**
     * {@link ConcurrentLock}
     */
    @Pointcut("@annotation(com.laxqnsys.core.aspect.lock.ConcurrentLock))")
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
        StringBuffer sb = new StringBuffer();
        Matcher matcher = pattern.matcher(key);
        while (matcher.find()) {
            Object value = Ognl.getValue(matcher.group(2), context);
            if (value == null) {
                log.error("解析失败: key={}, parameterNames = {}, parameters={}", key,
                    JSONUtil.toJsonStr(parameterNames), JSONUtil.toJsonStr(parameters));
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "key解析失败");
            }
            matcher.appendReplacement(sb, String.valueOf(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
