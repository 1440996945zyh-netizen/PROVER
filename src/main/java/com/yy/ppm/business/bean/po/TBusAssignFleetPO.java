package com.yy.ppm.business.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 指派物流车队(TBusAssignFleet)PO
 *
 * @author linqi
 * @since 2023-07-04 13:58:32
 */
@Setter
@Getter
public class TBusAssignFleetPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 指令ID
     */
    @NotNull(message = "指令id不能为空")
    private Long trustId;

    /**
     * 指令票货id
     */
    @NotNull(message = "指令票货id不能为空")
    private Long trustCargoId;

    /**
     * 物流车队ID
     */
    @NotNull(message = "物流车队id不能为空")
    private Long customerId;

    /**
     * 物流车队NAME
     */
    @NotBlank(message = "物流车队名称不能为空")
    private String customerName;

    /**
     * 计划件数
     */
    private Integer quantity;

    /**
     * 计划重量
     */
    @NotNull(message = "计划重量不能为空")
    private BigDecimal ton;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TBusAssignFleetPO that = (TBusAssignFleetPO) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(trustId, that.trustId).append(trustCargoId, that.trustCargoId).append(customerId, that.customerId).append(customerName, that.customerName).append(quantity, that.quantity).append(ton, that.ton).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(trustId).append(trustCargoId).append(customerId).append(customerName).append(quantity).append(ton).toHashCode();
    }
}
