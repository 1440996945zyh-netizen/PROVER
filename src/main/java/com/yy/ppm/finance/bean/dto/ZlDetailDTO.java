package com.yy.ppm.finance.bean.dto;


import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 发票表(TFdInvoice)DTO
 * @Description
 * @createTime 2023年09月15日 20:22:00
 */
@Data
public class ZlDetailDTO {

    private static final long serialVersionUID = 247146718870044966L;

    /**
     * 明细 ID
     */
    private String F_MXID;
    /**
     * 计费单 ID
     */
    private String F_JFDID;
    /**
     * 计费日期
     */
    private String F_JFRQ;

    /**
     * 费目代码
     */
    private String F_FMBH;
    /**
     * 费目名称
     */
    private String F_FMMC;
    /**
     * 计量单位
     */
    private String F_JLDW;
    /**
     * 数量（计费量）
     */
    private BigDecimal F_JFSL;
    /**
     * 币种
     */
    private String F_BZBH;
    /**
     * 原币单价
     */
    private BigDecimal F_YBDJ;
    /**
     * 原币计费金额
     */
    private BigDecimal F_YBJFJE;
    /**
     * 原币折扣额
     */
    private BigDecimal F_YBZKE;
    /**
     * 原币费用减免金额
     */
    private BigDecimal F_YBFYJMJE;
    /**
     * 原币应收金额
     */
    private BigDecimal F_YBYSJE;
    /**
     * 原币实际含税单价
     */
    private BigDecimal F_YBSJHSDJ;
    /**
     * 原币实际单价
     */
    private BigDecimal F_YBSJDJ;
    /**
     * 原币税额
     */
    private BigDecimal F_YBSE;
    /**
     * 原币收入金额
     */
    private BigDecimal F_YBSRJE;
    /**
     * 汇率
     */
    private BigDecimal F_HL;
    /**
     * 计费金额（含税金额）
     */
    private BigDecimal F_JFJE;
    /**
     * 优惠金额
     */
    private BigDecimal F_YHJE;
    /**
     * 减免金额
     */
    private BigDecimal F_JMJE;
    /**
     * 应收金额
     */
    private BigDecimal F_YSJE;
    /**
     * 税值
     */
    private BigDecimal F_SZ;
    /**
     * 税额
     */
    private BigDecimal F_SE;
    /**
     * 不含税金额
     */
    private BigDecimal F_BHSJE;
    /**
     * 备注
     */
    private String F_NOTE;
    /**
     * 摘要
     */
    private String F_ZY;
    /**
     * 客户编号
     */
    private String F_KHBH;
    /**
     * 客户名称
     */
    private String F_KHMC;
    /**
     * 部门编号
     */
    private String F_BMBH;
    /**
     * 部门名称
     */
    private String F_BMMC;
    /**
     * 发票号码
     */
    private String F_FPHM;
    /**
     * 资产编号
     */
    private String F_ZCBM;
    /**
     * 资产名称
     */
    private String F_ZCMC;
    /**
     * 租赁项目编码
     */
    private String F_ZLXMBH;
    /**
     * 租赁项目名称
     */
    private String F_ZLXMMC;
    /**
     * 金蝶业务类型编号
     */
    private String F_JDYWLXBH;
    /**
     * 金蝶业务类型名称
     */
    private String F_JDYWLXMC;
    /**
     * 租赁业务类型编号
     */
    private String F_ZLYWLXBH;
    /**
     * 租赁业务类型名称
     */
    private String F_ZLYWLXMC;
    /**
     * 金蝶往来类型编号
     */
    private String F_WLLXBH;
    /**
     * 金蝶往来类型名称
     */
    private String F_WLLXMC;
    /**
     * 固定资产租赁类型
     */
    private String F_GDZCZLLX;
    /**
     * 免税标志
     */
    private String F_MSLX;

}
