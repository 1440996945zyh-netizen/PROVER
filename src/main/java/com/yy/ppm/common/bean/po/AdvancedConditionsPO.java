package com.yy.ppm.common.bean.po;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AdvancedConditionsPO {
    /**
     * 字段名后端对应*/
    private String columnName;
    /**
     * 字段标签*/
    private String columnLabel;
    /**
     * 运算关系*/
    private String operator;
    /**
     * 运算关系标签*/
    private String operatorLabel;
    /**
     * 条件值*/
    private String value;
    /**
     * 开始值*/
    private String startValue;
    /**
     * 结束值*/
    private String endValue;
    /**
     * 字段类型*/
    private String colType ;
    /**
     * 日期格式*/
    private String dateFormat;
    /**
     * 业务功能字段是否是多选 */
    private String isBusinessMultiSelect;
    public AdvancedConditionsPO() {}

}
