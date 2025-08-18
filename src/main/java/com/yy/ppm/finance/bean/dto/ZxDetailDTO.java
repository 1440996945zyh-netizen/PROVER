package com.yy.ppm.finance.bean.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 发票表(TFdInvoice)DTO
 * @Description
 * @createTime 2023年09月15日 20:22:00
 */
@Data
public class ZxDetailDTO {

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
     * 收费方类型
     */
    private String F_SFFLX;
    /**
     * 费目代码
     */
    private String F_FMBH;
    /**
     * 费目名称
     */
    private String F_FMMC;
    /**
     * 计费单位
     */
    private String F_JFDW;
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
     * 含税单价（费率）
     */
    private BigDecimal F_HSDJ;
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
     * 提运单号
     */
    private String F_TYDH;
    /**
     * 作业委托单编号
     */
    private String F_ZYWTDBH;
    /**
     * 泊位
     */
    private String F_BOWEI;
    /**
     * 内外贸
     */
    private String F_NWMBS;
    /**
     * 进出口
     */
    private String F_JCKBS;
    /**
     * 停泊时长
     */
    private String F_TBSC;
    /**
     * 停泊时长时间单位
     */
    private String F_TBSCDW;
    /**
     * 装卸类型
     */
    private String F_ZXLX;
    /**
     * 包装方式
     */
    private String F_BZFS;
    /**
     * 运输方式
     */
    private String F_YSFS;
    /**
     * 靠泊日期
     */
    private String F_KBRQ;
    /**
     * 离泊日期
     */
    private String F_LBRQ;
    /**
     * 净吨
     */
    private String F_JD;
    /**
     * 作业项目
     */
    private String F_ZYXM;
    /**
     * 堆存天数
     */
    private String F_DCTS;
    /**
     * 船号
     */
    private String F_SHIPNO;
    /**
     * 中文船名
     */
    private String F_SHIPMC;
    /**
     * 英文船名
     */
    private String F_SHIPMCENG;
    /**
     * 账单编号
     */
    private String F_ZDBH;
    /**
     * 航次
     */
    private String F_HANGCI;
    /**
     * 货类编号
     */
    private String F_HLBH;
    /**
     * 货类名称
     */
    private String F_HLMC;
    /**
     * 货物编码
     */
    private String F_HWBM;
    /**
     * 货物名称
     */
    private String F_HWMC;
    /**
     * 货物交接地址
     */
    private String F_HWJJDZ;
    /**
     * 管输时间
     */
    private String F_GSSJ;
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
     * 生产货类编号
     */
    private String F_SCHLBH;
    /**
     * 生产货类名称
     */
    private String F_SCHLMC;
    /**
     * 生产货物编码
     */
    private String F_SCHWBM;
    /**
     * 生产货物名称
     */
    private String F_SCHWMC;
    /**
     * 发票号码
     */
    private String F_FPHM;
    /**
     * 免税标志
     */
    private String F_MSLX;
    /**
     * 包干费类型
     */
    private String F_BGFLX;
    /**
     * 船名航次
     */
    private String F_CMHC;

}
