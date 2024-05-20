package com.laxqnsys.core.util.spel;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author wuzhenhong
 * @date 2024/5/14 8:48
 */
public class StandardBeanExpressionResolverExt extends StandardBeanExpressionResolver {

    public StandardBeanExpressionResolverExt() {
        this.setExpressionPrefix("${");
        this.setExpressionSuffix("}");
    }

    @Override
    public Object evaluate(String value, BeanExpressionContext evalContext) throws BeansException {
        return super.evaluate(value, evalContext);
    }

    protected void customizeEvaluationContext(StandardEvaluationContext evalContext) {

        evalContext.addPropertyAccessor(new BeanExpressionContextAccessorExt());

    }
}