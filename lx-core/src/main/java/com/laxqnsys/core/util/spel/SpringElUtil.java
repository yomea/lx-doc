package com.laxqnsys.core.util.spel;

import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

/**
 * @author wuzhenhong
 * @date 2024/5/14 8:48
 */
public class SpringElUtil {

    private static final StandardBeanExpressionResolverExt SBERE = new StandardBeanExpressionResolverExt();
    private static ConfigurableBeanFactory beanFactory;

    public static final void setBeanFactory(ConfigurableBeanFactory beanFactory) {
        SpringElUtil.beanFactory = beanFactory;
    }

    public static final Object evaluate(String expression, Map<String, Object> context) {
        BeanExpressionContextExt contextExt;
        if (Objects.isNull(SpringElUtil.beanFactory)) {
            contextExt = new BeanExpressionContextExt();
        } else {
            contextExt = new BeanExpressionContextExt(SpringElUtil.beanFactory);
        }
        contextExt.setAllObject(context);
        return SBERE.evaluate(expression, contextExt);
    }

    public static final Object evaluate(String expression, ConfigurableBeanFactory beanFactory,
        Map<String, Object> context) {
        BeanExpressionContextExt contextExt = new BeanExpressionContextExt(beanFactory);
        contextExt.setAllObject(context);
        return SBERE.evaluate(expression, contextExt);
    }

    public static void main(String[] args) {

        BeanExpressionContextExt contextExt = new BeanExpressionContextExt();

        DocFileFolder folder = new DocFileFolder();
        folder.setName("xxxxx");
        contextExt.setObject("a", 2);
        contextExt.setObject("b", 10);
        contextExt.setObject("folder", folder);
        contextExt.setObject("contextExt", contextExt);

//        Object value = SBERE.evaluate("a+b > 20", contextExt);
        Object v = SBERE.evaluate("${folder.name = 'hello world!'}", contextExt);
        Object v1 = SBERE.evaluate("folder.setName('hello world! baby!')", contextExt);
        Object v2 = SBERE.evaluate("contextExt.setObject('a', 'a')", contextExt);
        Object v3 = SBERE.evaluate("${aa = 'bb'}", contextExt);
        Object value = SBERE.evaluate("asdsfsf_${folder.name}", contextExt);
        System.out.println(value);
    }
}