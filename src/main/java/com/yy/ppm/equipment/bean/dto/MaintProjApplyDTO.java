package com.yy.ppm.equipment.bean.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 维修项目申请DTO
 * @author system
 */
@Data
public class MaintProjApplyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 使用部门ID
     */
    private Long usingDeptId;

    /**
     * 使用部门名称
     */
    private String usingDeptName;

    /**
     * 设备类型id(设备小类)
     */
    private Long equipTypeId;

    /**
     * 设备ID
     */
    private Long equipId;

    /**
     * 设备名称
     */
    private String equipName;

    /**
     * 申请单号(系统自动生成:XMSQ-YYYYMMDD-四位顺序号)
     */
    private String appNumber;

    /**
     * 申请类型(1:定额，2:非定额)
     */
    private String appType;

    /**
     * 申请内容(文本域)
     */
    private String appContent;

    /**
     * 维修单位ID
     */
    private Long maintenanceUnitId;

    /**
     * 维修单位名称
     */
    private String maintenanceUnitName;

    /**
     * 预算金额(含税)(根据维修项目含税金额计算(定额)，或手输(非定额)
     */
    private BigDecimal budgetAmount;

    /**
     * 备注(文本域)
     */
    private String remark;

    /**
     * 创建人
     */
    private Long createdUser;

    /**
     * 创建时间
     */
    private Timestamp createdTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 修改时间
     */
    private Timestamp updateTime;

    // ========== 扩展字段（适配你之前的查询需求） ==========
    /**
     * 申请单号+维修单位名称拼接字段（APP_NUMBER + 维修单位名称）
     */
    private String appUnitName;
}
