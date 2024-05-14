package com.laxqnsys.core.handler;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.laxqnsys.core.constants.CommonCons;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.stereotype.Component;
 
@Component
//定义转换器支持的JAVA类型
@MappedTypes(LocalDateTime.class)
//定义转换器支持的数据库类型
@MappedJdbcTypes(value = JdbcType.TIMESTAMP, includeNullJdbcType = true)
public class CustomLocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {
 
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType)
            throws SQLException {
        if (parameter != null) {
            ps.setTimestamp(i, Timestamp.valueOf(parameter));
        }
    }
 
    @Override
    public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Timestamp target = rs.getTimestamp(columnName);
        if (Objects.isNull(target)) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(target.getTime()), ZoneId.systemDefault());
    }
 
    @Override
    public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String target = rs.getString(columnIndex);
        if (StringUtils.isBlank(target)) {
            return null;
        }
        // 日期值后面多了.0，去掉
        target = target.substring(0, target.length() - 2);
        return LocalDateTime.parse(target, CommonCons.YYYY_MM_SS_HH_MM_SS);
    }
 
    @Override
    public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String target = cs.getString(columnIndex);
        if (StringUtils.isBlank(target)) {
            return null;
        }
        // 日期值后面多了.0，去掉
        target = target.substring(0, target.length() - 2);
        return LocalDateTime.parse(target, CommonCons.YYYY_MM_SS_HH_MM_SS);
    }
}