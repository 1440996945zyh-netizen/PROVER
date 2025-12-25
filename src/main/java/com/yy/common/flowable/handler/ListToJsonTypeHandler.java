package com.yy.common.flowable.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MyBatis TypeHandler: List<String> ↔ Oracle CLOB 类型转换器
 *
 * 职责:
 * - 将 Java List<String> 序列化为 JSON 字符串存入数据库 CLOB 字段
 * - 将数据库 CLOB 字段的 JSON 字符串反序列化为 Java List<String>
 *
 * @author system
 * @date 2025-12-25
 */
@Slf4j
@MappedTypes({List.class})
@MappedJdbcTypes({JdbcType.CLOB})
public class ListToJsonTypeHandler extends BaseTypeHandler<List<String>> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Java -> Database (写入)
     * 将 List<String> 转换为 JSON 字符串写入 PreparedStatement
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        try {
            // 将 List 序列化为 JSON 字符串
            String json = OBJECT_MAPPER.writeValueAsString(parameter);

            log.debug("TypeHandler setNonNullParameter: List<String> -> JSON, size: {}",
                    parameter != null ? parameter.size() : 0);

            // 写入参数
            ps.setString(i, json);

        } catch (Exception e) {
            log.error("List转JSON失败, 参数位置: {}", i, e);
            throw new SQLException("无法将List<String>转换为JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Database -> Java (读取 - 按列名)
     * 从 ResultSet 按列名读取 CLOB 字段并转换为 List<String>
     */
    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return toList(json);
    }

    /**
     * Database -> Java (读取 - 按列索引)
     * 从 ResultSet 按列索引读取 CLOB 字段并转换为 List<String>
     */
    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return toList(json);
    }

    /**
     * Database -> Java (读取 - 存储过程)
     * 从 CallableStatement 中读取 CLOB 字段并转换为 List<String>
     */
    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return toList(json);
    }

    /**
     * 通用转换方法: JSON字符串 -> List<String>
     *
     * @param json JSON 字符串
     * @return List<String> 或空列表
     */
    private List<String> toList(String json) {
        // 处理 null 或空字符串
        if (json == null || json.trim().isEmpty()) {
            log.debug("TypeHandler toList: JSON为空，返回空列表");
            return new ArrayList<>();
        }

        try {
            // JSON 反序列化为 List<String>
            List<String> result = OBJECT_MAPPER.readValue(
                    json,
                    new TypeReference<List<String>>() {}
            );

            log.debug("TypeHandler toList: JSON -> List<String>, size: {}", result.size());

            return result;

        } catch (Exception e) {
            log.error("JSON转List失败, JSON内容: {}", json, e);
            // 转换失败返回空列表，避免数据丢失
            return new ArrayList<>();
        }
    }
}
