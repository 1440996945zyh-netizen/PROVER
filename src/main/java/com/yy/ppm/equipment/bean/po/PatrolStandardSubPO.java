package com.yy.ppm.equipment.bean.po;

import lombok.Data;

import java.io.Serializable;

/**
 * 巡检标准子表
 */
@Data
public class PatrolStandardSubPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主表ID */
    private Long parentId;

    /** 检查内容 */
    private String checkContent;

    /** 合格条件 */
    private String qualifyCondition;

    /** 检查方法 */
    private String checkMethod;
}