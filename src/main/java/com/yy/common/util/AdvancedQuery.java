package com.yy.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yy.ppm.common.bean.po.AdvancedConditionsPO;
import com.yy.ppm.common.bean.po.AdvancedQueryPO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedQuery  {
    // 使用静态 ObjectMapper 避免重复创建（线程安全）
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 数据库类型常量
    public static final String DATABASE_ORACLE = "oracle";
    public static final String DATABASE_MYSQL = "mysql";

    // 默认数据库类型，可以通过setter方法修改
    private static String databaseType = DATABASE_ORACLE;

    /**
     * 设置数据库类型
     */
    public static void setDatabaseType(String dbType) {
        if (DATABASE_ORACLE.equalsIgnoreCase(dbType) || DATABASE_MYSQL.equalsIgnoreCase(dbType)) {
            databaseType = dbType.toLowerCase();
        }
    }

    public static String getDatabaseType() {
        return databaseType;
    }

    /**
     * 处理条件组，生成 MyBatis 可用的条件列表
     */
    public static List<Map<String, Object>> processConditions(String conditionGroupsJson) {
        // 原有逻辑保持不变
        if (conditionGroupsJson == null || conditionGroupsJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            // 将 JSON 字符串解析为 List<AdvancedQueryPO>
            List<AdvancedQueryPO> conditionGroups = objectMapper.readValue(
                    conditionGroupsJson,
                    new TypeReference<List<AdvancedQueryPO>>() {}
            );


            if (conditionGroups == null || conditionGroups.isEmpty()) {
                return new ArrayList<>();
            }

            List<Map<String, Object>> processedConditions = new ArrayList<>();
            int globalIndex = 0;

            for (int i = 0; i < conditionGroups.size(); i++) {
                AdvancedQueryPO advancedQueryPO = conditionGroups.get(i);
                List<Map<String, Object>> groupConditions = processConditionGroup(advancedQueryPO, i, globalIndex);
                processedConditions.addAll(groupConditions);
                // 更新全局索引
                globalIndex += groupConditions.size();
            }
            return processedConditions;
        } catch (Exception e) {
            // 解析失败时抛出异常（或返回空列表，根据业务需求调整）
            throw new IllegalArgumentException("Invalid conditionGroups JSON format: " + conditionGroupsJson, e);
        }
    }

    private static List<Map<String, Object>> processConditionGroup(AdvancedQueryPO advancedQueryPO, int groupIndex, int startGlobalIndex) {

        List<Map<String, Object>> result = new ArrayList<>();
        int currentGlobalIndex = startGlobalIndex;

        if (advancedQueryPO.getConditions() == null || advancedQueryPO.getConditions().isEmpty()) {
            return result;
        }

        // 添加组开始标记
        Map<String, Object> groupStart = new HashMap<>();
        groupStart.put("type", "GROUP_START");
        groupStart.put("connectType", advancedQueryPO.getConnectType());
        groupStart.put("filterType", advancedQueryPO.getFilterType());
        groupStart.put("groupIndex", groupIndex);
        result.add(groupStart);
        currentGlobalIndex++;

        // 处理组内每个条件
        for (int j = 0; j < advancedQueryPO.getConditions().size(); j++) {
            AdvancedConditionsPO condition = advancedQueryPO.getConditions().get(j);
            Map<String, Object> processedCondition = processSingleCondition(
                    condition, groupIndex, j, advancedQueryPO, currentGlobalIndex);
            if (processedCondition != null) {
                result.add(processedCondition);
                currentGlobalIndex++;
            }
        }

        // 添加组结束标记
        Map<String, Object> groupEnd = new HashMap<>();
        groupEnd.put("type", "GROUP_END");
        groupEnd.put("groupIndex", groupIndex);
        result.add(groupEnd);

        return result;
    }

    private static Map<String, Object> processSingleCondition(
            AdvancedConditionsPO condition,
            int groupIndex,
            int conditionIndex,
            AdvancedQueryPO advancedQueryPO,
            int globalIndex) {

        Map<String, Object> processed = new HashMap<>();

        // 基础信息
        processed.put("columnName", condition.getColumnName());
        processed.put("operator", condition.getOperator());
        processed.put("value", condition.getValue());
        processed.put("startValue", condition.getStartValue());
        processed.put("endValue", condition.getEndValue());
        processed.put("dateFormat", condition.getDateFormat());
        processed.put("filterType", advancedQueryPO.getFilterType());
        processed.put("isBusinessMultiSelect", condition.getIsBusinessMultiSelect());
        processed.put("type", "GROUP_CONDITION");

        // 生成 SQL 片段 - 使用全局索引
        String sqlSnippet = generateSqlSnippet(condition, globalIndex);
        processed.put("sqlSnippet", sqlSnippet);

        // 索引信息
        processed.put("groupIndex", groupIndex);
        processed.put("conditionIndex", conditionIndex);
        processed.put("globalIndex", globalIndex); // 添加全局索引便于调试

        return processed;
    }

    private static String generateSqlSnippet(AdvancedConditionsPO condition, int globalIndex) {
        String columnName = condition.getColumnName();
        String operator = condition.getOperator();
        String dateFormat = condition.getDateFormat();

        // 使用全局索引作为参数前缀
        String paramPrefix = "processedConditions[" + globalIndex + "]";

        // 如果提供了日期格式，则对时间字段进行特殊处理
        if (dateFormat != null && !dateFormat.trim().isEmpty()) {
            return generateDateSqlSnippet(condition, globalIndex, columnName, operator, dateFormat, paramPrefix);
        }

        // 非时间字段的原有逻辑
        switch (operator) {
            case "equals":
                // 特殊处理：当 isBusinessMultiSelect 等于 0 且colType为6时
                if (condition.getIsBusinessMultiSelect() != null && "0".equals(condition.getIsBusinessMultiSelect()) && "6".equals(condition.getColType())) {
                    if (DATABASE_MYSQL.equals(databaseType)) {
                        return String.format("FIND_IN_SET(%s, #{%s.value}) = 0", columnName, paramPrefix);
                    } else {
                        return String.format("INSTR(',' || #{%s.value} || ',', ',' || %s || ',') > 0", paramPrefix, columnName);
                    }
                }
                return String.format("%s = #{%s.value}", columnName, paramPrefix);

            case "notEquals":
                //特殊处理：当 isBusinessMultiSelect 等于 0 且colType为6时
                if (condition.getIsBusinessMultiSelect() != null && "0".equals(condition.getIsBusinessMultiSelect()) && "6".equals(condition.getColType())) {
                    return generateMultiValueNotEqualsSql(columnName, paramPrefix, condition);
                }
                return String.format("%s != #{%s.value}", columnName, paramPrefix);

            case "greaterThan":
                return String.format("%s > #{%s.value}", columnName, paramPrefix);

            case "lessThan":
                return String.format("%s < #{%s.value}", columnName, paramPrefix);

            case "greaterEqualsThan":
                return String.format("%s >= #{%s.value}", columnName, paramPrefix);

            case "lessEqualsThan":
                return String.format("%s <= #{%s.value}", columnName, paramPrefix);

            case "empty":
                return String.format("(%s IS NULL OR %s = '')", columnName, columnName);

            case "notEmpty":
                if (DATABASE_MYSQL.equals(databaseType)) {
                    return String.format("(%s IS NOT NULL AND %s != '')", columnName, columnName);
                } else {
                    return String.format("(%s IS NOT NULL AND LENGTH(TRIM(%s)) > 0)", columnName, columnName);
                }

            case "contain":
                if (DATABASE_MYSQL.equals(databaseType)) {
                    return String.format("%s LIKE CONCAT('%%', #{%s.value}, '%%')", columnName, paramPrefix);
                } else {
                    return String.format("%s LIKE '%%' || #{%s.value} || '%%'", columnName, paramPrefix);
                }

            case "notContain":
                if (DATABASE_MYSQL.equals(databaseType)) {
                    return String.format("%s NOT LIKE CONCAT('%%', #{%s.value}, '%%')", columnName, paramPrefix);
                } else {
                    return String.format("%s NOT LIKE '%%' || #{%s.value} || '%%'", columnName, paramPrefix);
                }

            case "interval":
                return String.format("%s BETWEEN #{%s.startValue} AND #{%s.endValue}",
                        columnName, paramPrefix, paramPrefix);

            case "equalsAny":
                return String.format("%s IN (#{%s.value})", columnName, paramPrefix);

            case "notEqualsAny":
                return String.format("%s NOT IN (#{%s.value})", columnName, paramPrefix);

            default:
                return String.format("%s = #{%s.value}", columnName, paramPrefix);
        }
    }



    /**
     * 生成多值不等于查询的 SQL 片段
     * 当 isBusinessMultiSelect 等于 0 且 colType 为 6 时
     */
    private static String generateMultiValueNotEqualsSql(String columnName, String paramPrefix, AdvancedConditionsPO condition) {
        // 根据数据库类型生成不同的 SQL
        if (DATABASE_MYSQL.equals(databaseType)) {
            return String.format("FIND_IN_SET(%s, #{%s.value}) = 0", columnName, paramPrefix);
        } else {
            return String.format("INSTR(',' || #{%s.value} || ',', ',' || %s || ',') = 0", paramPrefix, columnName);
        }
    }


    /**
     * 生成时间字段的 SQL 片段
     */
    private static String generateDateSqlSnippet(AdvancedConditionsPO condition, int globalIndex,
                                                 String columnName, String operator, String dateFormat,
                                                 String paramPrefix) {

        // 根据数据库类型生成不同的日期SQL
        if (DATABASE_MYSQL.equals(databaseType)) {
            return generateMySQLDateSqlSnippet(condition, globalIndex, columnName, operator, dateFormat, paramPrefix);
        } else {
            return generateOracleDateSqlSnippet(condition, globalIndex, columnName, operator, dateFormat, paramPrefix);
        }
    }

    /**
     * 生成 MySQL 时间字段的 SQL 片段
     */
    private static String generateMySQLDateSqlSnippet(AdvancedConditionsPO condition, int globalIndex,
                                                      String columnName, String operator, String dateFormat,
                                                      String paramPrefix) {

        // 将 Java 格式转换为 MySQL 格式
        String mysqlDateFormat = convertToMySQLDateFormat(dateFormat);

        // MySQL 使用 STR_TO_DATE 函数将字符串转换为日期
        String strToDateParam = String.format("STR_TO_DATE(#{%s.value}, '%s')", paramPrefix, mysqlDateFormat);

        switch (operator) {
            case "equals":
                return String.format("%s = %s", columnName, strToDateParam);

            case "notEquals":
                return String.format("%s != %s", columnName, strToDateParam);

            case "greaterThan":
                return String.format("%s > %s", columnName, strToDateParam);

            case "lessThan":
                return String.format("%s < %s", columnName, strToDateParam);

            case "greaterEqualsThan":
                return String.format("%s >= %s", columnName, strToDateParam);

            case "lessEqualsThan":
                return String.format("%s <= %s", columnName, strToDateParam);

            case "interval":
                // MySQL 时间区间查询
                String startStrToDate = String.format("STR_TO_DATE(#{%s.startValue}, '%s')", paramPrefix, mysqlDateFormat);
                String endStrToDate = String.format("STR_TO_DATE(#{%s.endValue}, '%s')", paramPrefix, mysqlDateFormat);
                return String.format("%s BETWEEN %s AND %s", columnName, startStrToDate, endStrToDate);

            case "empty":
                return String.format("(%s IS NULL)", columnName);

            case "notEmpty":
                return String.format("(%s IS NOT NULL)", columnName);

            default:
                return String.format("%s = %s", columnName, strToDateParam);
        }
    }

    /**
     * 生成 Oracle 时间字段的 SQL 片段
     */
    private static String generateOracleDateSqlSnippet(AdvancedConditionsPO condition, int globalIndex,
                                                       String columnName, String operator, String dateFormat,
                                                       String paramPrefix) {

        // 将 Java 格式转换为 Oracle 格式
        String oracleDateFormat = convertToOracleDateFormat(dateFormat);

        // Oracle 使用 TO_DATE 函数将字符串转换为日期
        String toDateParam = String.format("TO_DATE(#{%s.value}, '%s')", paramPrefix, oracleDateFormat);

        switch (operator) {
            case "equals":
                return String.format("%s = %s", columnName, toDateParam);

            case "notEquals":
                return String.format("%s != %s", columnName, toDateParam);

            case "greaterThan":
                return String.format("%s > %s", columnName, toDateParam);

            case "lessThan":
                return String.format("%s < %s", columnName, toDateParam);

            case "greaterEqualsThan":
                return String.format("%s >= %s", columnName, toDateParam);

            case "lessEqualsThan":
                return String.format("%s <= %s", columnName, toDateParam);

            case "interval":
                // Oracle 时间区间查询
                String startToDate = String.format("TO_DATE(#{%s.startValue}, '%s')", paramPrefix, oracleDateFormat);
                String endToDate = String.format("TO_DATE(#{%s.endValue}, '%s')", paramPrefix, oracleDateFormat);
                return String.format("%s BETWEEN %s AND %s", columnName, startToDate, endToDate);

            case "empty":
                return String.format("(%s IS NULL)", columnName);

            case "notEmpty":
                return String.format("(%s IS NOT NULL)", columnName);

            default:
                return String.format("%s = %s", columnName, toDateParam);
        }
    }

    /**
     * 将 Java 日期格式转换为 Oracle 日期格式
     */
    private static String convertToOracleDateFormat(String javaDateFormat) {
        if (javaDateFormat == null || javaDateFormat.isEmpty()) {
            return "YYYY-MM-DD HH24:MI:SS"; // 默认格式
        }

        // 标准化格式字符串（去除多余空格）
        String normalizedFormat = javaDateFormat.trim();

        // 根据不同的格式进行转换
        switch (normalizedFormat) {
            case "YYYY-MM-DD HH:mm:ss":
                return "YYYY-MM-DD HH24:MI:SS";
            case "YYYY-MM-DD HH:mm":
                return "YYYY-MM-DD HH24:MI";
            case "YYYY-MM-DD":
                return "YYYY-MM-DD";
            case "YYYY-MM":
                return "YYYY-MM";
            default:
                // 对于其他格式，使用通用转换逻辑
                return convertCustomOracleDateFormat(normalizedFormat);
        }
    }

    /**
     * 将 Java 日期格式转换为 MySQL 日期格式
     */
    private static String convertToMySQLDateFormat(String javaDateFormat) {
        if (javaDateFormat == null || javaDateFormat.isEmpty()) {
            return "%Y-%m-%d %H:%i:%S"; // 默认格式
        }

        // 标准化格式字符串（去除多余空格）
        String normalizedFormat = javaDateFormat.trim();

        // 根据不同的格式进行转换
        switch (normalizedFormat) {
            case "YYYY-MM-DD HH:mm:ss":
                return "%Y-%m-%d %H:%i:%S";
            case "YYYY-MM-DD HH:mm":
                return "%Y-%m-%d %H:%i";
            case "YYYY-MM-DD":
                return "%Y-%m-%d";
            case "YYYY-MM":
                return "%Y-%m";
            default:
                // 对于其他格式，使用通用转换逻辑
                return convertCustomMySQLDateFormat(normalizedFormat);
        }
    }

    /**
     * 处理 Oracle 自定义日期格式的转换
     */
    private static String convertCustomOracleDateFormat(String javaDateFormat) {
        String oracleFormat = javaDateFormat;

        // 检查是否包含时间部分
        boolean hasTimePart = oracleFormat.contains("HH") || oracleFormat.contains("mm") || oracleFormat.contains("ss");

        if (hasTimePart) {
            // 将分钟格式从 mm 转换为 MI（Oracle 中分钟用 MI）
            oracleFormat = oracleFormat.replace("mm", "MI");

            // 将秒格式从 ss 转换为 SS（Oracle 中秒用 SS）
            oracleFormat = oracleFormat.replace("ss", "SS");

            // 确保小时使用 24 小时制
            if (!oracleFormat.contains("HH24")) {
                oracleFormat = oracleFormat.replace("HH", "HH24");
            }
        }

        return oracleFormat;
    }

    /**
     * 处理 MySQL 自定义日期格式的转换
     */
    private static String convertCustomMySQLDateFormat(String javaDateFormat) {
        String mysqlFormat = javaDateFormat;

        // 将 Java 格式转换为 MySQL 格式
        mysqlFormat = mysqlFormat.replace("YYYY", "%Y")
                .replace("MM", "%m")
                .replace("DD", "%d")
                .replace("HH", "%H")
                .replace("mm", "%i")
                .replace("ss", "%S");

        return mysqlFormat;
    }
}
