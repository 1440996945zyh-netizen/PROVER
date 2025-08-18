package com.yy.ppm.produce.bean.dto.workTicket;

import com.yy.ppm.produce.bean.po.TPrdWorkTicketDetailPO;
import com.yy.ppm.produce.bean.po.TPrdWorkTicketEquipmentPO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-18 9:28
 */
@Getter
@Setter
public class TPrdWorkTicketDetailDTO extends TPrdWorkTicketDetailPO {

    /**
     * 作业票设备
     */
    private List<TPrdWorkTicketEquipmentPO> equipments;

    /**
     * 是否核销作业量
     */
    private String isSettLe;

    /**
     * 源区域
     */
    private String srms;
    /**
     * 目标区域
     */
    private String srmt;
    private String trustNo;
    private String shipVoyageName;
    private String loadUnload;
    private String equipmentTypeName;
    private String equipmentNo;
    private Long tpwtId;
    /**
     * 二次过磅时间
     */
    private Date weighOutDt;
    /**
     * 一次过磅时间
     */
    private Date weighInDt;
    /**
     * 车号
     */
    private String truckPlate;
    /**
     * 过磅量
     */
    private BigDecimal weightGoods;
    /**
     * 货物信息 返回前端展示用
     */
    private String cargoInfoName;
    /**
     * 票货号
     */
    private String cargoInfoNo;
    /**
     * 前端禁止标志位
     */
    private String disabled;
    private String allotType;
    /**
     * 是否理货
     */
    private String isAllotLabel;
    private BigDecimal laborNumber;

    /** 位置，字典 WORK_POSITION(前场、水平、后场、辅助) */
    private String workPositionCode;
    /** 位置 */
    private String workPositionName;

    //转运机械信息
    private Long transportEquipmentId;
    private String transportEquipmentNo;
    private String transportEquipmentTypeCode;
    private String transportEquipmentTypeName;
    private Long transportDeptId;
    private String transportDeptName;
    private Long tranMachineDeptId;
    private String tranMachineDeptName;
//普通机械信息
    private Long machineDeptId;
    private String machineDeptName;

    private Long deptParentId;
    private String deptParentName;
    /**
     * 分配总量
     */
    private String tmpAllTon;




    private String groupKey;
//    private String groupId;

    /**
     * 作业公司ID
     */
    private Long companyId;

    /**
     * 作业公司名称
     */
    private String companyName;

    //整船调整头部汇总
    private BigDecimal tmpTopTon;
    //整船调整底部汇总
    private BigDecimal tmpBotTon;
    private Long workPlanId;
    private Long ticketDetailId;

    private Long trustId;

    private Boolean canEdit;


}
