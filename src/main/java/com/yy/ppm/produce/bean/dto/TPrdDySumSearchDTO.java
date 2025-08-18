package com.yy.ppm.produce.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (TPrdDySum)SearchDTO
 * @Description TODO
 * @createTime 2024年12月03日 17:07:00
 */
@Data
public class TPrdDySumSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -92747882951998564L;

    /***/
    private BigDecimal id;
    /***/
    private BigDecimal tsptId;
    /**
     * 指令id
     */
    private BigDecimal trustId;
    /**
     * 航次子表id
     */
    private BigDecimal shipVoyageItemId;
    /**
     * 港区
     */
    private String portName;
    /**
     * 通知单号
     */
    private String trustNo;
    /**
     * scn
     */
    private String scn;
    /**
     * 船名航次
     */
    private String shipVoyageName;
    /**
     * 包装
     */
    private String packingName;
    /**
     * 内外贸
     */
    private String tradeType;
    /**
     * 货物code
     */
    private String cargoCode;
    /**
     * 货名
     */
    private String cargoName;
    /**
     * 外付合同类型
     */
    private String outwardTypeName;
    /**
     * 部门id
     */
    private String deptId;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 吨数
     */
    private String ton;
    /**
     * 库场审核状态，0：未审核，1：已审核
     */
    private String kcStatus;
    /**
     * 调度审核状态，0：未审核，1：已审核
     */
    private String ddStatus;
    /**
     * 外付审核状态，0：未审核，1：已审核
     */
    private String wfStatus;
    /***/
    private String createByName;
    /***/
    private String updateByName;
}

