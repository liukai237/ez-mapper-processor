package com.iakuil.em;

import com.iakuil.em.util.JsonUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis JSON字段处理器基类
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public abstract class AbstractJsonTypeHandler<T> extends BaseTypeHandler<T> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JsonUtils.bean2Json(parameter));
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String data = rs.getString(columnName);
        return isBlank(data) ? null : JsonUtils.json2bean(data, (Class<T>) getRawType());
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String data = rs.getString(columnIndex);
        return isBlank(data) ? null : JsonUtils.json2bean(data, (Class<T>) getRawType());
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String data = cs.getString(columnIndex);
        return isBlank(data) ? null : JsonUtils.json2bean(data, (Class<T>) getRawType());
    }

    private boolean isBlank(String str) {
        return str == null || "".equals(str);
    }
}