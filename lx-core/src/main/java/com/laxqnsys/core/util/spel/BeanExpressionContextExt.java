package com.laxqnsys.core.util.spel;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * @author wuzhenhong
 * @date 2024/5/14 8:48
 */
public class BeanExpressionContextExt extends BeanExpressionContext {

    private static final ConfigurableBeanFactory BEAN_FACTORY = new DefaultListableBeanFactory();

    private final Map<String, Object> variables = new HashMap<>();

    public BeanExpressionContextExt() {
        super(BEAN_FACTORY, null);
    }

    public BeanExpressionContextExt(Scope scope) {
        super(BEAN_FACTORY, scope);
    }

    public BeanExpressionContextExt(ConfigurableBeanFactory beanFactory) {
        this(beanFactory, null);
    }

    public BeanExpressionContextExt(ConfigurableBeanFactory beanFactory, Scope scope) {
        super(beanFactory, scope);
    }

    @Override
    public boolean containsObject(String key) {
        return (getBeanFactory().containsBean(key) ||
            (getScope() != null && getScope().resolveContextualObject(key) != null) || variables.containsKey(key));
    }

    @Override
    public Object getObject(String key) {
        if (getBeanFactory().containsBean(key)) {
            return getBeanFactory().getBean(key);
        } else if (getScope() != null) {
            return getScope().resolveContextualObject(key);
        } else {
            return variables.get(key);
        }
    }

    public void setObject(String key, Object val) {
        variables.put(key, val);
    }

    public void setAllObject(Map<String, Object> params) {
        variables.putAll(params);
    }

    public void clear() {
        variables.clear();
    }

    public Map<String, Object> getAllObjectWithNotBean() {

        return new java.util.HashMap<>(variables);
    }
}