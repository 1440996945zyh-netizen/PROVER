package com.yy.ppm.tallyExtrinsic.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @ClassName 作业计划表(TPrdWorkPlan)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月21日 16:21:00
 */
@Data
public class TPrdWorkPlanPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 856472478874014075L;

    /** 主键ID */
    private Long id;
    /** 作业公司ID */
    private Long companyId;
    /** 作业公司NAME */
    private String companyName;
    /** 日期 */
    private String workDate;
    /** 班次（字典） */
    private String classCode;
    /** 班次NAME */
    private String className;
    /** 指令ID */
    private Long trustId;
    /** 指令NO */
    private String trustNo;
    private String scn;
    /** 航次ID */
    private Long shipvoyageId;
    /** 航次子表id */
    private Long shipvoyageItemId;
    /** 舱口 */
    private String hatch;
    /** 计划编号 */
    private String planNo;
    /** 计划类型同作业过程 */
    private String planType;
    /** 作业过程代码 */
    private String processCode;
    /** 作业过程名称 */
    private String processName;
    /** 作业内容(零工申请用) */
    private String workContent;
    /** 申请部门id */
    private Long deptId;
    /** 申请部门name */
    private String deptName;
    /** 计划件数 */
    private Long quantityPlan;
    /** 计划重量 */
    private BigDecimal tonPlan;
    /** 计划开工时间 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date startTimePlan;
    /** 计划完工时间 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endTimePlan;
    /** 开工时间 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date startWorkTime;
    /** 完工时间 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endWorkTime;
    /** 作业要求 */
    private String remark;
    /** 状态，10：未审核：20：已审核30作业中40停工50完工 */
    private String status;
    /** 泊位ID */
    private Long berthId;
    /** 泊位name */
    private String berthName;
    /** 前沿理货员ID */
    private String frontTallyBy;
    /** 理货员姓名 */
    private String frontTallyByName;
    /** 后场理货员ID */
    private String backTallyBy;
    /** 理货员姓名 */
    private String backTallyByName;
    /** 调度员ID */
    private String dispatchBy;
    /** 调度员name */
    private String dispatchByName;
    /** 审核者-ID */
    private Long examineBy;
    /** 审核者-姓名 */
    private String examineByName;
    /** 审核时间 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date examineTime;

    /** 列表显示用前沿机械 */
    private String equipmentNamesFront;
    private String equipmentNamesBack;
    private String equipmentNamesAssist;
    /** 转运 */
    private String equipmentNamesReshipment;
    private String massNamesSource;
    private String massNamesTarget;

    /** 转运类型   1客户要求，2内部转运 */
    private String reshipmentTypeCode;
    /** 票货id， 转运用 */
    private Long busCargoInfoId;

    /** 货种 杂项使用 */
    private String cargoCategoryCode;
    private String cargoCategoryName;
    private String voyage;
    /**
     * 公司编码
     */
    private String deptCode;
    private String cargoCode;
    private String portCode;
    private String packageCode;
    private String cargoOwnerId;

    private List<Long> ids;

}

