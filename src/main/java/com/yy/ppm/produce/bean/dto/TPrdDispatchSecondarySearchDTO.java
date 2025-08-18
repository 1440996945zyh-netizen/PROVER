package com.yy.ppm.produce.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 作业计划派工表（二次配工）(TPrdDispatchSecondary)SearchDTO
 * @Description TODO
 * @createTime 2023年07月30日 18:16:00
 */
@Getter
@Setter
@ToString
public class TPrdDispatchSecondarySearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -30254908951253906L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 计划ID
     */
    private Long workPlanId;
    /**
     * 计划ID
     */
    private String trustId;
    /**
     * 派工类型，1机械、2劳务
     */
    private Long dispatchType;
    /**
     * 位置，字典 WORK_POSITION(前场、水平、后场、辅助)
     */

    private String workPositionCode;
    /**
     * 位置
     */
    private String workPositionName;
    /**
     * 设备类型ID
     */
    private Long equipmentTypeId;
    /**
     * 设备类型名称
     */
    private String equipmentTypeName;
    /**
     * 设备ID
     */
    private Long equipmentId;
    /**
     * 设备编号
     */
    private String equipmentNo;
    /**
     * 数量
     */
    private String num;
    /**
     * 人员ID
     */
    private Long operatorsId;
    /**
     * 票货ID
     */
    private Long cargoInfoId;
    /**
     * 人员姓名
     */
    private String operatorsName;
    /**
     * 部门ID
     */
    private Long deptId;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建者-姓名
     */
    private String createByName;
    /**
     * 更新者-姓名
     */
    private String updateByName;

    /**
     * 部门编码
     */
    private String deptCode;

    /**
     * 部门No
     */
    private String deptNo;

    /**
     * 作业过程
     */
    private String processCode;
    
    /**
     * 是否配工部门
     */
    private String canDispatchDept;
    private List<String> type;
}

