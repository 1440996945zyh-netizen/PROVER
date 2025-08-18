package com.yy.ppm.appWorkNew.bean.dto;


import com.yy.ppm.produce.bean.dto.TPrdDispatchDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanLocationDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanTrustDTO;
import com.yy.ppm.produce.bean.po.TPrdWorkPlanPO;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName 作业计划表(TPrdWorkPlan)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月21日 16:21:00
 */
@Data
public class TPrdWorkPlanDTO extends TPrdWorkPlanPO {

    private static final long serialVersionUID = 537996647070379955L;

    /** 作业计划id */
    private Long id;

    /** 状态 */
    private String statusLabel;
    private String workStatus;


    /** 转运类型 */
    private String reshipmentTypeLabel;

    /** 前场机械 */
    List<TPrdDispatchDTO> equmentListFront;
    /** 后场机械 */
    List<TPrdDispatchDTO> equmentListBack;
    /** 辅助机械 */
    List<TPrdDispatchDTO> equmentListAssist;
    /** 倒运机械 */
    List<TPrdDispatchDTO> equmentListReshipment;
    /** 船舶计划的指令信息 */
    List<TPrdWorkPlanTrustDTO> trustList;

    /** 源垛位 */
    List<TPrdWorkPlanLocationDTO> locationListSource;
    // 修改反显用
    List<Long> regionIdsSource;
    // 主列表显示的
    String massNamesSource;

    /** 目标垛位 */
    List<TPrdWorkPlanLocationDTO> locationListTarget;
    // 修改反显用
    List<Long> regionIdsTarget;
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
    private String voyage;
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

    private List<String>  statementStatusList;

    //支持多个计划进行派工
    List<Long> planIds;

    /**
     * 包装
     */
    private String packingCode;
    private String targetCd;
    private String sourceCd;
    private String isDirectAccess;
    private List<Map<String,Object>> cargoInfoList;


}
