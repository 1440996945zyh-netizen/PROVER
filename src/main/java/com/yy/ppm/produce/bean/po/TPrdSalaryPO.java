package com.yy.ppm.produce.bean.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * (TPrdSalary)PO
 *
 * @author linqi
 * @since 2023-08-23 09:54:41
 */
@Setter
@Getter
public class TPrdSalaryPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 作业票子表ID
     */
    private Long workTicketDetailId;

    /**
     * 作业公司ID
     */
    private Long companyId;

    /**
     * 作业公司NAME
     */
    private String companyName;



    /**
     * 日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date  salaryMonth;

    /**
     * 班次code字典 WORK_SCHEDULE
     */
    private String classCode;

    /**
     * 班次NAME
     */
    private String className;

    /**
     * "计件工资项目名称代码（字典：PIECE_PROJECT 装载机、现场管理、内燃平衡重式叉车、港口门座起重机）"
     */
    private String pieceProjectCode;

    /**
     * "计件工资项目名称（字典：PIECE_PROJECT 装载机、现场管理、内燃平衡重式叉车、港口门座起重机）"
     */
    private String pieceProjectName;

    /**
     * 作业过程代码
     */
    private String processCode;

    /**
     * 作业过程名称
     */
    private String processName;

    /**
     * 货物信息
     */
    private Long cargoInfoId;
    private String cargoCode;
    private String cargoName;

    /**
     * 子作业过程代码
     */
    private String processDetailCode;

    /**
     * 子作业过程名称
     */
    private String processDetailName;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 人员-ID
     */
    private Long userBy;

    /**
     * 人员-姓名
     */
    private String userByName;

    /**
     * 分配系数
     */
    private Integer coefficient;

    /**
     * 计件单价ID
     */
    private Long salaryPriceId;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 件数
     */
    private Integer quantity;

    /**
     * 吨数(数量)
     */
    private BigDecimal ton;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 状态：字典待审核、已审核(SALARY_STATUS)
     */
    private String salaryStatusCode;

    /**
     * 状态：字典(SALARY_STATUS)
     */
    private String salaryStatusName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 计件汇总ID
     */
    private Long salarySummaryId;

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

    /**计件工资类型*/
    private String salaryTypeCode;

    /**计件工资类型名称*/
    private String salaryTypeName;
    /**
     * 船名航次
     */
    private String shipVoyage;
    private Long workTicketId;

    @TableField(exist = false)
    private String salaryStatusCodeWhere;


    /**
     * 开始日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    /**
     * 结束日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    private String shipVoyageItemId;

    /**
     * 零工计划ID
     */
    private Long oddPlanId;
    /**
     * 是否零工
     */
    private String isOdd;

}

