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
 * @Author: fanxianjin
 * @Desc: 设备报废PO
 * @Date: 2026/2/28 14:29
 */
@Getter
@Setter
@ToString
public class EEquipScrapPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 工单号
     */
    private String scrapCode;

    /**
     * 标题
     */
    private String title;

    /**
     * 所属公司ID
     */
    private Long useCompanyId;

    /**
     * 所属部门ID
     */
    private Long useOrgId;

    /**
     * 申请人ID
     */
    private Long applyUser;

    /**
     * 申请原因
     */
    private String applyReason;

    /**
     * 执行人ID
     */
    private Long executeUser;

    /**
     * 执行完成时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date executeFulfilTime;

    /**
     * 审批状态（1审批中，2审批成功，3审批未通过）
     */
    private Long status;

    /**
     * 是否删除（0否，1是）
     */
    private Long deleted;

    /**
     * 流程ID（预留）
     */
    private String flowId;

}
