package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备调拨PO
 * @author system
 */
@Getter
@Setter
@ToString
public class EEquipAllocatePO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 调拨编号
     */
    private String allocateCode;

    /**
     * 调拨标题
     */
    private String title;

    /**
     * 调入单位id
     */
    private Long toCompanyId;

    /**
     * 调入部门id
     */
    private Long toOrgId;

    /**
     * 审批状态：1审批中；2审批成功；3审批未通过
     */
    private Long status;

    /**
     * 调拨时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date allocateTime;

    /**
     * 申请人
     */
    private Long applyUser;

    /**
     * 申请原因
     */
    private String applyReason;

    /**
     * 执行人
     */
    private Long executeUser;

    /**
     * 调拨完成时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date allocateFulfilTime;

    /**
     * 是否删除（0.否，1.是）
     */
    private Long delFlag;

    /**
     * 流程id
     */
    private String flowId;

}
