package com.yy.ppm.dispatch.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 航次子表(TDisShipvoyageItem)PO
 *
 * @author linqi
 * @since 2023-07-04 13:49:34
 */
@Setter
@Getter
public class TDisShipvoyageItemPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /** 渤海通id */
    private Long boHaiTongId;

    /**
     * 航次ID
     */
    private Long shipvoyageId;

    /**
     * 进出口(进口  出口)
     */
    private String impExp;

    /**
     * 贸别，内贸、外贸
     */
    @NotBlank(message = "贸别不能为空")
    private String tradeType;

    /**
     * 装卸,装、卸
     */
    @NotBlank(message = "装卸不能为空")
    private String loadUnload;

    /**
     * 航次
     */
    @NotBlank(message = "航次不能为空")
    private String voyage;

    /**
     * 作业公司ID
     */
    @NotNull(message = "作业公司id不能为空")
    private Long companyId;

    /**
     * 作业公司名称
     */
    @NotBlank(message = "作业公司名称不能为空")
    private String companyName;

    /**
     * 货种代码，多选
     */
    private String cargoCategoryCode;

    /**
     * 货种名称，多选
     */
    @NotBlank(message = "货名不能为空")
    private String cargoCategoryName;

    /**
     * 货量
     */
    @NotNull(message = "货量不能为空")
    private BigDecimal cargoNum;

    /**
     * 客户ID（船代）
     */
    @NotNull(message = "客户id不能为空")
    private Long customerId;

    /**
     * 客户名称（船代）
     */
    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String shipPhone;

    /**
     * 预缴金额
     */
    private BigDecimal paymentAmount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态CODE，预报、接收、抵锚..,（字典SHIP_STATUS）
     */
    private String shipStatusCode;

    /**
     * 状态名称 ，预报、接收、抵锚..,（字典SHIP_STATUS）
     */
    private String shipStatusName;

    /**
     * 开工时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date workStartTime;

    /**
     * 完工时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date workEndTime;

    /**
     * 票货的真正完货时间    计算每日堆存量用
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date realClearDate;

    /** 转水前船名 */
    private String preChangeShipName;

    /** 转水前编号 */
    private String preChangeShipNo;

    /**
     * 客户代码（船代）
     */
    private String customerCode;

    private String firCargoCate;
    private String secCargoCate;


}
