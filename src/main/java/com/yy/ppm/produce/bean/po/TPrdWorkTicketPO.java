package com.yy.ppm.produce.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class TPrdWorkTicketPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 作业公司ID
     */
    private Long companyId;

    /**
     * 作业公司名称
     */
    private String companyName;

    /**
     * 作业计划ID
     */
    @NotNull(message = "作业计划ID不能为空")
    private Long workPlanId;

    /**
     * 作业票类型
     */
    private String type;

    /**
     * 作业过程代码
     */
    private String processCode;

    /**
     * 作业过程名称
     */
    private String processName;

    /**
     * 日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workDate;

    /**
     * 班次code字典
     */
    private String classCode;

    /**
     * 班次名称
     */
    private String className;

    /**
     * 状态:字典待审核、已审核(WORK_TICKET_STATUS)
     */
    private String workTicketStatus;

    /**
     * 状态:字典(WORK_TICKET_STATUS)
     */
    private String workTicketStatusName;

    /**
     * 审核人
     */
    private Long examineBy;

    /**
     * 审核人姓名
     */
    private String examineByName;

    /**
     * 审核时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date examineTime;

    /**
     * 件数
     */
    private Integer quantity;

    /**
     * 吨数(数量)
     */
    private BigDecimal ton;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    private String ticketType;
    private Long examineByUp;
    /**
     * 审核时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date examineTimeUp;

    /**
     * 审核人姓名
     */
    private String examineByNameUp;
    private String soure;


    /**
     * 是否老作业票  0否1是
     */
    private String isOld;
    /**
     * 分配类型
     */
    private String allotType;
}
