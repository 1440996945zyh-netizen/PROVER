package com.yy.ppm.produce.bean.dto.workTicket;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-14 16:15
 */
@Setter
@Getter
public class TPrdWorkPlanDTO {

    /**
     * 作业计划ID
     */
    private Long id;
    private Long ticketId;
    private Long companyId;
    private Long trustId;

    /**
     * 作业公司
     */
    private String companyName;
    private String planType;
    private String shipvoyageLabel;

    /**
     * 日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date workDate;

    /**
     * 班次编码
     */
    private String classCode;

    /**
     * 班次
     */
    private String className;

    /**
     * 船名航次
     */
    private String shipNameVoyage;

    /**
     * 舱口数
     */
    private String hatchNum;
    private String hatch;

    /**
     * 计划编号
     */
    private String planNo;

    /**
     * 作业过程编码
     */
    private String processCode;

    /**
     * 作业过程
     */
    private String processName;

    /**
     * 申请部门
     */
    private String deptName;

    /**
     * 计划件数
     */
    private Integer quantityPlan;
    private Integer laborNum;

    /**
     * 计划重量
     */
    private BigDecimal tonPlan;

    /**
     * 计划开工时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date startTimePlan;

    /**
     * 计划完工时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date endTimePlan;

    /**
     * 开工时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date startWorkTime;

    /**
     * 完工时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date endWorkTime;

    /**
     * 作业要求
     */
    private String remark;

    /**
     * 状态编码
     */
    private Integer status;

    /**
     * 状态
     */
    private String statusLabel;

    /**
     * 泊位
     */
    private String berthName;

    /**
     * 前沿理货员
     */
    private String frontTallyByName;

    /**
     * 后场理货员
     */
    private String backTallyByName;

    /**
     * 调度员
     */
    private String dispatchByName;

    /**
     * 前沿机械
     */
    private String equipmentNamesFront;

    /**
     * 后场机械
     */
    private String equipmentNamesBack;

    /**
     * 辅助机械
     */
    private String equipmentNamesAssist;

    /**
     * 倒运机械
     */
    private String equipmentNamesReshipment;

    /**
     * 是否签票
     */
    private String isSigned;

    /**
     * 签票状态
     */
    private String workTicketStatusName;
    private String cargoName;
    private String cargoCode;
    private String cargoAgentName;
    private String cargoOwnerName;
    private String cargoOwnerId;
    private String loadUnload;
    private String massNamesSource;
    private String massNamesTarget;
    private String trustNo;
    private String examineBy;
    private String flowStatus;// 流机队配工状态
    private String fixedStatus;// 固机队配工状态
    private String laborStatus;// 装卸队配工状态
    private String scn;
    private String cargoInfoNo;
    private String isSignedKc;
    private String shipVoyageIds;
    private String examineByName;
    private String ticketType;
    private String type;


    /**
     * 录入人
     */
    private Long createBy;

    /**
     * 录入人姓名
     */
    private String createByName;

    /**
     * 录入时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 录入人姓名
     */
    private String updateByName;

    /**
     * 录入时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
