package com.yy.ppm.statement.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.validate.AddGroup;
import com.yy.common.validate.EditGroup;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 杂项计费基础po
 * @author yangcl
 * */
@Data
public class TMiscBillingPO extends BasePO {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 费率id
     */
    @NotNull(message = "费率不可为空",groups = {AddGroup.class, EditGroup.class})
    private String rateItemCode;

    /**
     * 费率名称
     */
    @NotBlank(message = "费率名称不可为空",groups = {AddGroup.class, EditGroup.class})
    private String rateName;

    /**
     * 费率
     */
    @NotNull(message = "费率不可为空",groups = {AddGroup.class, EditGroup.class})
    private BigDecimal rate;

    /**
     * 税率
     */
    @NotNull(message = "税率不可为空",groups = {AddGroup.class, EditGroup.class})
    private BigDecimal taxRate;

    /**
     * 计费日期
     */
    @NotNull(message = "计费日期不可为空",groups = {AddGroup.class, EditGroup.class})
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date billDate;

    /**
     * 航次id
     */
    private Long voyageId;

    /**
     * 船名航次
     */
    private String shipVoyage;

    /**
     * 计费量
     */
    @NotNull(message = "计费量不可为空",groups = {AddGroup.class, EditGroup.class})
    private BigDecimal billQuantity;

    /**
     * 计费量2（船舶净吨）
     */
    private BigDecimal billQuantity2;

    /**
     * 计费金额
     */
    @NotNull(message = "计费金额不可为空",groups = {AddGroup.class, EditGroup.class})
    private BigDecimal amountMoney;

    /**
     * 税额
     */
    @NotNull(message = "税额不可为空",groups = {AddGroup.class, EditGroup.class})
    private BigDecimal taxAmount;

    /**
     * 指令id
     */
    private Long trustOrderId;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 关联结算单ID
     */
    private Long statementId;
    /**
     * 关联结算单ID,进保税区生成的杂项费使用
     */
    private Long otherStatementId;

    /**
     * 状态
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

    private Long publishBy;

    private String publishByName;

    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd hh:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    private Date publishTime;

    @NotNull(message = "作业公司id不可为空",groups = {AddGroup.class, EditGroup.class})
    private Long companyId;

    @NotNull(message = "作业公司名称不可为空",groups = {AddGroup.class, EditGroup.class})
    private String companyName;

    /**
     * 费率ID
     */
    @NotNull(message = "费率ID不可为空",groups = {AddGroup.class, EditGroup.class})
    private Long rateId;

    //单位
    private String unitCode;
    private String unitName;

    private String processCode;

    private String processName;
    /**
     * 票货id
     */
    private Long cargoInfoId;
    /**
     * 票货名称
     */
    private String cargoInfoName;

    private String taxationInvoiceCode;
    private String taxationInvoiceName;

    /***
     * 计费人相关
     */
    private Long statementBy;
    private String statementByName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date statementTime;

    /**
     * VEHICLE_TRANSFER_FLAG
     * 是否二次到运费
     */
    private String  vehicleTransferFlag;
    /**
     * 回执驳回
     */
    private Long rejectBy;
    private String rejectByName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date rejectTime;
    private String rejectReason;

    /**
     * 是否是混配自动生成的混配费用
     */
    private String isCargoMix;

}

