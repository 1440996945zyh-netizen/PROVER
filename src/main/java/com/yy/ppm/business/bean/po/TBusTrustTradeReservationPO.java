package com.yy.ppm.business.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 集疏港指令预约(TBusTrustTradeReservation)PO
 *
 * @author linqi
 * @since 2023-07-04 14:02:12
 */
@Setter
@Getter
public class TBusTrustTradeReservationPO extends BasePO {

    /**
     * 主键ID
     */
    @NotNull(message = "主键id不能为空")
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
     * 预约件数
     */
    private Integer quantity;

    /**
     * 预约重量
     */
    @NotNull(message = "预约重量不能为空")
    private BigDecimal ton;

    /**
     * 开始时间（有效期起）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @NotNull(message = "开始时间不能为空")
    private Date startTime;

    /**
     * 结束时间（有效期起）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @NotNull(message = "结束时间不能为空")
    private Date endTime;

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
     * 状态 (1:待审核10：已审核 20：冻结)
     */
    private String status;

    /**
     * 审核人-ID
     */
    private Long examineBy;

    /**
     * 审核人-姓名
     */
    private String examineByName;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date examineTime;

    /**
     * 货主ID
     */
    private String tbtcCargoOwnerId;

    /**
     * 货主名称
     */
    private String tbtcCargoOwnerName;

    /**
     * 货代id
     */
    private String tbtcCargoAgentId;

    /**
     * 货代名称
     */
    private String tbtcCargoAgentName;

    /**
     * 货物代码
     */
    private String tbtcCargoCode;

    /**
     * 货物名称
     */
    private String tbtcCargoName;

    /**
     * 指令编号
     */
    private String tbtTrustNo;

    /**
     * 作业过程代码
     */
    private String tbtProcessCode;

    /**
     * 作业过程名称
     */
    private String tbtProcessName;

    /**
     * 作业公司id
     */
    private String tbtCompanyId;

    /**
     * 作业公司名称
     */
    private String tbtCompanyName;

    /**
     * 航次ID
     */
    private String tbtShipvoyageId;

    /**
     * 航次子表ID
     */
    private String tbtShipvoyageItemId;

    /**
     * 船名
     */
    private String tbtShipName;

    /**
     * 航次
     */
    private String tdsiVoyage;

    /**
     * 船名航次
     */
    private String shipNameVoyage;
}
