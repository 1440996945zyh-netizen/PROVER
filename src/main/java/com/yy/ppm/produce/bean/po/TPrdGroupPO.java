package com.yy.ppm.produce.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * (TPrdGroupPO)PO
 *
 * @author chenfs
 * @since 2023-10-12 09:54:41
 */
@Setter
@Getter
public class TPrdGroupPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 计划ID
     */
    private Long planId;


    /**
     * 分组名称
     */
    @NotNull(message = "分组名称不能为空")
    private String groupName;


    /**
     * 作业过程代码
     */
    @NotNull(message = "作业过程代码不能为空")
    private String processCode;

    /**
     * 作业过程名称
     */
    @NotNull(message = "作业过程名称不能为空")
    private String processName;

    /**计件工资类型*/
    @NotNull(message = "计件工资类型不能为空")
    private String salaryTypeCode;

    /**计划类型CD*/
    @NotNull(message = "计划类型不能为空")
    private String planTypeCd;

    /**计划类型NAME*/
    private String planTypeName;

    /**计件工资类型名称*/
    @NotNull(message = "计件工资类型名称不能为空")
    private String salaryTypeName;
    private List<TPrdGroupDetailPO> detailPOList;

    /**
     * 日期
     */
    private String workDate;

    /**
     * 班次编码
     */
    private String classCode;
}

