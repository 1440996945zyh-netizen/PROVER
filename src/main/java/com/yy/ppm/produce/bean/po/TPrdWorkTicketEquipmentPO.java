package com.yy.ppm.produce.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Setter
@Getter
public class TPrdWorkTicketEquipmentPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 作业票明细ID
     */
    private Long workTicketDetailId;

    /**
     * 设备类型编码
     */
    @NotBlank(message = "设备类型编码不能为空")
    private String equipmentTypeCode;

    /**
     * 设备类型名称
     */
    @NotBlank(message = "设备类型名称不能为空")
    private String equipmentTypeName;

    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空")
    private Long equipmentId;

    /**
     * 设备编号
     */
    @NotBlank(message = "设备编号不能为空")
    private String equipmentNo;

    /**
     * 备注
     */
    private String remark;
    /**
     * 作业计划id
     */
    private Long workPlanId;
}
