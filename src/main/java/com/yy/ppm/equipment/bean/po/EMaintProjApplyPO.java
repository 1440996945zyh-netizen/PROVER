package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 设备调拨PO
 * @author system
 */
@Getter
@Setter
@ToString
public class EMaintProjApplyPO extends BasePO implements Serializable {

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
    //状态
    private String status;

    /**
     * 创建人
     */
    private Long createdUser;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 修改人
     */
    private Long updateUser;

    /**
     * 修改时间
     */
    private Date updateTime;
    private String isSettlement;

}
