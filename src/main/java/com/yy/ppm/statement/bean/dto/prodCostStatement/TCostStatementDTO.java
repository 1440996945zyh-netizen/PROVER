package com.yy.ppm.statement.bean.dto.prodCostStatement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.statement.bean.po.TCostStatementDetailPO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-14 14:36
 */
@Setter
@Getter
public class TCostStatementDTO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 作业公司id
     */
    @NotNull(message = "作业公司ID不能为空")
    private Long companyId;

    /**
     * 作业公司NAME
     */
    @NotBlank(message = "作业公司名称不能为空")
    private String companyName;

    /**
     * 结算单编号
     */
    private String statementNo;

    /**
     * 客户ID
     */
    @NotNull(message = "客户（货主）ID不能为空")
    private Long customerId;

    /**
     * 客户名称
     */
    @NotBlank(message = "客户（货主）名称不能为空")
    private String customerName;
    private String nextPortName;

    /**
     * 结算单类型（10.船舶货方结算单、20.陆集陆疏货方结算单 ）30.船方计费 40.杂项计费
     */
    private String type;

    /**
     * 指令ID
     */
    private Long trustId;

    /**
     * 指令票货id
     */
    private Long trustCargoId;

    /**
     * 货物清单ID
     */
    @NotNull(message = "交接清单ID不能为空")
    private Long handoverlistId;

    /**
     * 航次ID
     */
    @NotNull(message = "船舶预报ID不能为空")
    private Long shipvoyageId;

    /**
     * 航次子表ID
     */
    @NotNull(message = "船舶航次ID不能为空")
    private Long shipvoyageItemId;

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
     * 贸别，内贸、外贸
     */
    @NotBlank(message = "贸别编码不能为空")
    private String tradeType;

    /**
     * 结算日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date settlementDate;

    /**
     * 状态（1.生产结算 2.商务结算 3.计费审核 4.开票）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否最终结算 0否/1是
     */
    @NotBlank(message = "是否最终结算不能为空")
    private String isFinal;

    /**
     * 结算状态Label
     */
    private String statusLabel;

    /**
     * 结算单明细
     */
    @NotEmpty(message = "结算单明细不能为空")
    private List<TCostStatementDetailPO> details;

    /**
     * 是否最终结算Label
     */
    private String isFinalLabel;

    /**
     * 结算量 当前结算单下的计费总量 number
     */
    private BigDecimal number;

    /**
     * 进保税区货量
     */
    private BigDecimal bondedAreaTon;

    private String shipNameVoyage;

    private Long cargoInfoId;

    private String cargoInfoNo;

    //陆集陆疏显示集港过磅量
    private BigDecimal JGWeight;
    //陆集陆疏显示疏港过磅量
    private BigDecimal SGWeight;
}
