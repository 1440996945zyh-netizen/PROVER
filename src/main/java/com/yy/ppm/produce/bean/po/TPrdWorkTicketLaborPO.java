package com.yy.ppm.produce.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Setter
@Getter
public class TPrdWorkTicketLaborPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 作业票ID
     */
    private Long workTicketId;

    /**
     * 部门ID
     */
    @NotNull(message = "部门ID不能为空")
    private Long deptId;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    /**
     * 吨数(数量)
     */
    @NotNull(message = "吨数（数量）不能为空")
    private BigDecimal ton;

    /**
     * 备注
     */
    private String remark;
}
