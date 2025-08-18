package com.yy.ppm.produce.bean.po;


import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName 作业计划一次派工表(TPrdDispatch)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月21日 16:22:00
 */
@Data
public class TPrdDispatchPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -68786067671593301L;

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
    /** 设备类型CODE */
    private String equipmentTypeCode;
    /** 设备类型名称 */
    private String equipmentTypeName;
    /** 数量 */
    private Integer num;
    /** 多个门机ID */
    private String equipmentIds;
    /** 多个门机Name */
    private String equipmentNames;
    /** 备注 */
    private String remark;

}

