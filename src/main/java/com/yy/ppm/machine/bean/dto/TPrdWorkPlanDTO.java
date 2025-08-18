package com.yy.ppm.machine.bean.dto;

import com.yy.ppm.produce.bean.po.TPrdWorkPlanPO;

import lombok.Data;

/**
 * 作业指令
 * @author zcc
 */
@Data
public class TPrdWorkPlanDTO extends TPrdWorkPlanPO {

    private static final long serialVersionUID = 537996647070379955L;
    
    String massNamesTarget;

    Long busCargoInfoId;

    /** 劳务数量 */
    private Integer laborNum;

    /** 船舶仓库数量 */
    private Integer hatchNum;
    /** 船舶仓库数量 */
    private String loadUnload;
    /** 航次 */
    private String shipvoyageLabel;
    /** 船名 */
    private String shipName;
    private String hatch;
    private String shipVoyageIds;

    /** 更新类型 1：库场派工，派理货员；2：库场派工，指派场地 ； 3:工班计划派工：派调度员*/
    private String updateType;
    /** 船代 */
    private String customerName;

    private String batchId;
    /**
     * 批量派工标志位 1 批量 2 单选派工
     */
    private Long batchFlag;

    private String settlementBasisName;
    private String cargoOwnerName;
    private String cargoAgentName;
    private String cargoName;
    private String cargoInfoNo;

    private String shipNameVoyages;
    private String massNames;
    private String portName;
    private String portCode;

//    private List<Long> ids;
    private String flowStatus;// 流机队配工状态
    private String fixedStatus;// 固机队配工状态
    private String laborStatus;// 装卸队配工状态
    private String trustInfoNo;
    private String cargoOwnerIds;
    private String  cargoCode;
    /**
     * 包装
     */
    private String packingCode;
    
    /**
     * 展示的数据
     */
    private String workPlanLabel;
    
    /**
     * 展示的数据2
     */
    private String workPlanLabel2;
    
    /**
     * 展示的数据3
     */
    private String workPlanLabel3;
    
    /**
     * 展示的数据4
     */
    private String workPlanLabel4;
    
    /**
     * 磅单备注
     */
    private String poundRemark;
}
