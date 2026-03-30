package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 物资采购主表PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EPatrolPlanPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 主键ID
     */
    private Long id;

    /**
     * 巡检计划编号
     */
    private String planCode;

    /**
     * 巡检计划名称
     */
    private String planName;

    /**
     * 巡检路线ID
     */
    private Long routeId;

    /**
     * 类型:1:天,2:周,3:月,4:年
     */
    private String patrolType;

    /**
     * 初始日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date initialDate;

    /**
     * 设置日期
     */
    private String setDate;

    /**
     * 是否单次:1:是,2:否
     */
    private String isSingle;

    /**
     * 巡检员ID
     */
    private Long patrolId;

    /**
     * 巡检员NAME
     */
    private String patrolName;

    /**
     * 最近生成任务时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date recentlyTaskDate;

    /**
     * 状态:0:停止,1:启用
     */
    private String status;


    private String timeLimit;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private Long updateBy;

    /**
     * 修改时间
     */
    private Date updateTime;
}

