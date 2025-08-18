package com.yy.ppm.common.service;

import com.yy.ppm.common.bean.dto.CheckDTO;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.system.bean.dto.SysDeptDTO;

import java.util.HashMap;
import java.util.List;

public interface CommonService {

    /**
     * 根据表名、列名、列值删除
     * @param tableName
     * @param columnName
     * @param columnValue
     * @return
     */
    public int delete(String tableName, String columnName, String columnValue);

    /**
     * 根据表名删除
     * @param tableName
     * @return
     */
    public int deleteAll(String tableName);

    /**
     * 根据Gid删除
     * @param tableName
     * @param id
     * @return
     */
    public int deleteById(String tableName, Long id);

    /**
     * 根据多个Gid删除
     * @param tableName
     * @param ids
     * @return
     */
    public int deleteByIds(String tableName, List<Long> ids);

    /**
     * 验证简单的重复字段
     * @param tableName
     * @param conditionColNm
     * @param conditionColVal
     * @param gid
     * @param errorKeyWord
     * @param otherCondition
     * @return
     */
    public boolean isRepeate(String tableName, String conditionColNm, String conditionColVal, String gid, String errorKeyWord, List<CheckDTO> keyValue, String ... otherCondition);

    /**
     * 校验字段是否重复，重复会抛出异常，否则返回false
     * @param tableName
     * @param conditionColNm
     * @param conditionColVal
     * @param gid
     * @param errorKeyWord
     * @param keyValue
     * @param otherCondition
     * @return
     */
    public boolean isRepeat(String tableName, String conditionColNm, String conditionColVal, String gid, String errorKeyWord, List<CheckDTO> keyValue, String ... otherCondition);

   /**
     * 获取下一个值
     * @param tableName
     * @param columnName
     * @return
     */
    public int getNextValue(String tableName, String columnName, String otherCondition);

    /**
     * 获取编号
     *
     * @param businessType 编号类型
     * @param parentCodeVal 父字段的值
     * @param otherVal 其他条件
     * @return
     */
    public String getAutoNum(AutoNumEnum.BusinessAutoEnum businessType, String parentCodeVal);

}
