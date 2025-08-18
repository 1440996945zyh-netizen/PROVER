package com.yy.ppm.produce.bean.dto.workTicket;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import com.yy.ppm.produce.bean.po.TPrdWorkPlanPO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-14 16:10
 */
@Setter
@Getter
public class TPrdTicketSeconAllotQuery extends PageParameter {

    /** 主键ID */
    private Long id;
    /** 作业公司ID */
    private Long companyId;
    /** 作业公司NAME */
    private String companyName;
    /** 日期 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workDate;
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

    private Integer flag;
    private String LoginId;
    private List<Long> ids;
    private String cargoType; //1件货 2散货

    private String startDay;
    private String endDay;

    private String ticketType;
    /**
     * 分配类型
     */
    private String allotType;
    /**
     * 作业计划id
     */
    private Long workPlanId;

    /**
     * 不查询固机队的票
     */
    private String noGj;
    /**
     * 二次配工类型
     */
    private String dispatchType;
    /**
     * 货主id
     */
    private Long cargoOwnerId;
    /**
     * 货名
     */
    private String cargoCode;
    /**
     * 包装
     */
    private String packageCode;
    /**
     * 作业位置
     */
    private String portCode;

    /**
     * 分配类型过滤条件
     */
    private String isTicket;
    //流机队配工情况
    private String flowStatus;
    //装卸队配工情况
    private String laborStatus;

    private String shipName;
    private String voyage;
}
