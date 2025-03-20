package com.laxqnsys.core.other.util.ongl;

import cn.hutool.json.JSONUtil;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.ognl.Ognl;
import org.apache.ibatis.ognl.OgnlException;

/**
 * @author wuzhenhong
 * @date 2024/6/25 12:27
 */
@Slf4j
public class OnglUtils {

    // 非贪吃模式匹配
    private static final Pattern PATTERN = Pattern.compile("(\\$\\{)([\\w\\W]+?)(\\})");

    public static String evaluate(String express, Map<String, Object> context) throws OgnlException {

        StringBuffer sb = new StringBuffer();
        Matcher matcher = PATTERN.matcher(express);
        while (matcher.find()) {
            Object value = Ognl.getValue(matcher.group(2), context);
            if (value == null) {
                log.error("解析失败: express={}, context = {}", express,
                    JSONUtil.toJsonStr(context));
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "key解析失败");
            }
            matcher.appendReplacement(sb, String.valueOf(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
