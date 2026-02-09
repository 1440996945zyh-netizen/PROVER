package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * 物资申报PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialApplicationPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 申请单号
     */
    private String applicationNo;

    /**
     * 申报主题
     */
    private String applicationTitle;

    /**
     * 申报类型编码
     */
    private String applicationTypeCode;

    /**
     * 申报类型名称
     */
    private String applicationTypeName;

    /**
     * 定点服务类别编码
     */
    private String fixedServiceCategoryCode;

    /**
     * 定点服务类别名称
     */
    private String fixedServiceCategoryName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 状态
     */
    private String status;

    /**
     * 审批备注
     */
    private String approvalRemark;

    /**
     * 审批人ID
     */
    private Long approvalBy;

    /**
     * 审批人姓名
     */
    private String approvalByName;

    /**
     * 审批时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date approvalTime;
}

