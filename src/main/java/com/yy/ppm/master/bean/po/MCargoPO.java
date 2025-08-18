package com.yy.ppm.master.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * (MCargo)PO
 *
 * @author makejava
 * @date 2021-03-08 11:17:32
 */
@Getter
@Setter
@ToString
public class MCargoPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -97043204299000665L;

    /**主键*/
    private Long id;

    private String bhtId;

    /**货物编号 自动采番，根据货种算12位*/
    private String cargoCode;

    /**货名*/
    private String cargoName;

    /**所属货种代码*/
    private String cargoCategoryCode;

    /**作业模式，1件杂、2散杂、3木材*/
    private Integer workType;

    /**件杂货理货方式1件号0件数*/
    private Integer tally;

    /**货物颜色*/
    private String cargoColor;

    /**排序 */
    private Integer sortNum;

    /** 删除标志（0代表存在 1代表删除） */
    private String delFlag;

    private String shorthandCode;

    /** 场存节点 */
    private Integer updatePoint;

    /**计件工资类型*/
    private String yardSalaryTypeCode;

    /**计件工资类型名称*/
    private String yardSalaryTypeName;

    /**货物标识码*/
    private String sign;

    /**计件工资类型(调度)*/
    private String dispatchSalaryTypeCode;

    /**计件工资类型名称(调度)*/
    private String dispatchSalaryTypeName;

    /**计件工资类型(流机)*/
    private String flowSalaryTypeCode;

    /**计件工资类型名称(流机)*/
    private String flowSalaryTypeName;

    /**计件工资类型(固机)*/
    private String fixedSalaryTypeCode;

    /**计件工资类型名称(固机)*/
    private String fixedSalaryTypeName;

    /**
     * 浮动量(‰)
     */
    private Integer floatTon;

    /**
     * 状态 0停用/1启用
     */
    private String status;

    private String outwardType;
    private String outwardTypeName;
}
