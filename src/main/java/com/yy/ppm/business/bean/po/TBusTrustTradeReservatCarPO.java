package com.yy.ppm.business.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 集疏港指令预约车辆表(TBusTrustTradeReservatCar)PO
 *
 * @author linqi
 * @since 2023-07-04 14:05:56
 */
@Setter
@Getter
public class TBusTrustTradeReservatCarPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 指令ID
     */
    private Long trustId;

    /**
     * 集疏港指令车队预约id
     */
    private Long trustTradeReservationId;

    /**
     * 车牌号
     */
    @NotBlank(message = "车牌号不能为空")
    private String carNo;

    /**
     * 司机姓名
     */
    private String driverName;

    /**
     * 司机身份证号
     */
    private String driverIdCard;

    /**
     * 司机联系电话
     */
    private String driverPhone;

    /**
     * 车辆状态(10：正常 20：冻结)
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
