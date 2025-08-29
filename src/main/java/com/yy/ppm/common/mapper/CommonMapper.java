package com.yy.ppm.common.mapper;

import com.yy.ppm.common.bean.dto.CheckDTO;
import com.yy.ppm.common.bean.dto.SelectResultDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

//

public interface CommonMapper {

    /**
     * 根据表、列、gid删除
     *
     * @param tableName
     * @param columnName
     * @param columnValue
     * @return
     */
    int delete(@Param("tableName") String tableName, @Param("columnName") String columnName,
               @Param("columnValue") String columnValue);
    /**
     * 根据表删除
     * @param tableName
     * @return
     */
    int deleteAll(String tableName);
    /**
     * 根据Gid删除
     *
     * @param tableName
     * @param id
     * @return
     */
    int deleteById(@Param("tableName") String tableName, @Param("id") Long id);

    /**
     * 根据多个Gid删除
     *
     * @param tableName
     * @param ids
     * @return
     */
    int deleteByIds(@Param("tableName") String tableName, @Param("ids") List<Long> ids);

    int isDelFlagExists(@Param("tableName") String tableName);

    /**
     * 根据表名、列名、条件列名、条件列值、sql、要验证的列值 校验唯一性
     * @param tableName
     * @param conditionColNm
     * @param conditionColVal
     * @param id
     * @return
     */
    int checkRepeat(@Param("tableName") String tableName, @Param("conditionColNm") String conditionColNm,
                    @Param("conditionColVal") String conditionColVal,
                    @Param("id") String id,
                    @Param("list") List<CheckDTO> keyValues,
                    @Param("isDelFlagExists") int isDelFlagExists
    );

    /**
     * 根据表名、列名、条件列名、条件列值、sql、要验证的列值 校验唯一性
     * @param tableName
     * @param conditionColNm
     * @param conditionColVal
     * @param id
     * @return
     */
    int isRepeat(@Param("tableName") String tableName, @Param("conditionColNm") String conditionColNm,
                    @Param("conditionColVal") String conditionColVal,
                    @Param("id") String id,
                    @Param("list") List<CheckDTO> keyValues,
                    @Param("isDelFlagExists") int isDelFlagExists
    );

    /**
     * 根据表名和字段名获取该字段的下一个值
     * @param tableName
     * @param columnName
     * @return
     */
    int getNextValue(@Param("tableName") String tableName, @Param("columnName") String columnName, @Param("otherCondition") String otherCondition);

    /**
     * 动态组合查询下拉框
     * @param cd
     * @param nm
     * @param tableName
     * @param where
     * @param sort
     * @return
     */
    List<SelectResultDTO> dynamicDropDownBox(@Param("cd") String cd, @Param("nm") String nm,
                                             @Param("tableName") String tableName, @Param("where") String where, @Param("sort") String sort);

    /**
     * 根据模块信息获得流水号（不带年月日， 例如0001,00010001,000100010001
     * @param params
     * @return
     */
    String getAutoNum(Map<String, Object> params);

    /**
     * 查询行数
     *
     * @param tableName
     * @param columnName
     * @param columnValue
     * @return
     */
    int getCount(@Param("tableName") String tableName, @Param("columnName") String columnName,
                 @Param("columnValue") String columnValue);

    /**
     * 查询行数
     */
    int getCountByParentId(@Param("tableName") String tableName, @Param("parentId") Long parentId);
}

