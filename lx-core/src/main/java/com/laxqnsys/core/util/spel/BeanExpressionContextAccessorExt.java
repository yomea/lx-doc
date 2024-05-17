package com.laxqnsys.core.util.spel;

import org.springframework.context.expression.BeanExpressionContextAccessor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;

/**
 * @author wuzhenhong
 * @date 2024/5/14 8:48
 */
public class BeanExpressionContextAccessorExt extends BeanExpressionContextAccessor {

    @Override
    public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
        return true;
    }

    @Override
    public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {

        BeanExpressionContextExt beanExpressionContextExt = (BeanExpressionContextExt) target;

        beanExpressionContextExt.setObject(name, newValue);
    }

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class<?>[]{BeanExpressionContextExt.class};
    }

}