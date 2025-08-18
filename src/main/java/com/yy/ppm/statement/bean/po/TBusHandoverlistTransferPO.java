package com.yy.ppm.statement.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 交接清单(货权转移)PO
 *
 * @author linqi
 * @since 2023-09-07 10:40:47
 */
@Setter
@Getter
public class TBusHandoverlistTransferPO extends BasePO {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空")
    private Long id;

    /**
     * 类型（1.装卸船清单 2.陆集陆疏）
     */
    @NotBlank(message = "类型不能为空")
    private String type;

    /**
     * 票货id
     */
    @NotNull(message = "票货id不能为空")
    private Long cargoInfoId;
    /**
     * 票货号
     */
    private String cargoInfoNo;

    /**
     * 指令ID（只有陆集陆疏清单才会存在）
     */
    private Long trustId;

    /**
     * 指令票货id（只有陆集陆疏清单才会存在）
     */
    private Long trustCargoId;

    /**
     * 航次ID
     */
    private Long shipvoyageId;

    /**
     * 航次子表ID
     */
    private Long shipvoyageItemId;

    /**
     * 中文船名
     */
    private String shipName;

    /**
     * 航次
     */
    private String voyage;

    /**
     * 贸别，内贸、外贸
     */
    private String tradeType;

    /**
     * 装卸,装、卸
     */
    private String loadUnload;

    /**
     * 客户代码
     */
    private String customerCode;

    private String contractCode;

    private String contractItemId;

    /**
     * 货主代码
     */
    @NotNull(message = "货主ID不能为空")
    private Long cargoOwnerId;

    /**
     * 货主名称
     */
    @NotBlank(message = "货主名称不能为空")
    private String cargoOwnerName;

    /**
     * 货代代码
     */
    private Long cargoAgentId;

    /**
     * 货代名称
     */
    private String cargoAgentName;

    /**
     * 货物代码
     */
    @NotBlank(message = "货物编码不能为空")
    private String cargoCode;

    /**
     * 货名
     */
    @NotBlank(message = "货名不能为空")
    private String cargoName;

    /**
     * 件数
     */
    private Integer quantity;

    /**
     * 吨数
     */
    @NotNull(message = "吨数不能为空")
    private BigDecimal ton;

    /**
     * 体积吨数
     */
    private BigDecimal volumeTon;

    /**
     * 票数
     */
    private Integer ticketNum;

    /**
     * 结算状态（字典：STATEMENT_STATUS）
     */
    private String statementStatusCode;

    /**
     * 结算状态（字典：STATEMENT_STATUS 未结算、已预结、最终结算）
     */
    private String statementStatusName;

    /**
     * 备注
     */
    private String remark;

    private BigDecimal preWeight;
}

