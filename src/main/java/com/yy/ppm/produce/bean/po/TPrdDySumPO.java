package com.yy.ppm.produce.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (TPrdDySum)PO
 * @Description
 * @createTime 2024年12月03日 17:07:00
 */
@Data
public class TPrdDySumPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 512185085555872150L;

    /**
     *
     */
    private Long id;
    /**
     *
     */
    private String tsptId;
    /**
     * 指令id
     */
    private String trustId;
    /**
     * 航次子表id
     */
    private String shipVoyageItemId;
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
    private String outwardType;
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
    /**
     * hr审核状态，0：未审核，1：已审核
     */
    private String hrStatus;

}

