package com.laxqnsys.core.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

/**
 * @author wuzhenhong
 * @date 2024/5/20 9:21
 */
public class StringToLongConverter implements Converter<String, Long> {

    @Override
    public Long convert(@NonNull String source) {
        if (!StringUtils.hasText(source) || "\"\"".equals(source.trim()) || "''".equals(source.trim())) {
            return null;
        }
        return NumberUtils.parseNumber(source, Long.class);
    }
}
