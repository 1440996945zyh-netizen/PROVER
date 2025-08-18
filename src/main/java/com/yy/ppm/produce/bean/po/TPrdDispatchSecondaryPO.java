package com.yy.ppm.produce.bean.po;


import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @ClassName 作业计划派工表（二次配工）(TPrdDispatchSecondary)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月30日 18:16:00
 */
@Getter
@Setter
@ToString
public class TPrdDispatchSecondaryPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 349129774651500879L;

        /** 主键ID */
    private Long id;
            /** 计划ID */
    private Long workPlanId;
            /** 派工类型，1机械、2劳务 */
    private String dispatchType;
            /** 位置，字典 WORK_POSITION(前场、水平、后场、辅助) */
    private String workPositionCode;
            /** 位置 */
    private String workPositionName;
            /** 设备类型ID */
    private Long equipmentTypeId;
            /** 设备类型名称 */
    private String equipmentTypeName;
            /** 设备ID */
    private Long equipmentId;
            /** 设备编号 */
    private String equipmentNo;
            /** 人员ID */
    private Long operatorsId;
            /** 人员姓名 */
    private String operatorsName;
            /** 部门ID */
    private Long deptId;
            /** 部门名称 */
    private String deptName;
            /** 备注 */
    private String remark;

    /**
     * 已派工数量
     */
    private long numberCount;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 公司名称
     */
    private String companyName;
    
    /**
     * 子作业过程CODE
     */
    private String subProcessCode;

    /**
     * 子作业过程名称
     */
    private String subProcessName;
}

