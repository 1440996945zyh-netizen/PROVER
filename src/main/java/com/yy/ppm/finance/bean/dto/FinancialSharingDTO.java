package com.yy.ppm.finance.bean.dto;


import com.yy.ppm.finance.bean.po.TFdInvoicePO;
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
public class FinancialSharingDTO<T> {

    private static final long serialVersionUID = 247146718870044966L;

    /**
     * 计费单 ID
     */
    private String F_JFDID;
    /**
     * 公司编码
     */
    private String F_ZZJG;
    /**
     * 公司名称
     */
    private String F_ZZJGMC;
    /**
     * 单据来源系统
     */
    private String F_LYXTBH;
    /**
     * 是否合并开票
     */
    private String F_SFHBKP;
    /**
     * 计费单据类型
     */
    private String F_JFDJLX;
    /**
     * 附件个数
     */
    private int F_FSSL;
    /**
     * 是否暂估
     */
    private String F_SFZG;
    /**
     * 合同编号(租赁没有该字段)
     */
    private String F_HTBH;
    /**
     * 币种
     */
    private String F_BZBH;
    /**
     * 计费金额合计（含税金额）
     */
    private BigDecimal F_JFJEHJ;
    /**
     * 优惠金额合计
     */
    private BigDecimal F_YHJEHJ;
    /**
     * 减免金额合计
     */
    private BigDecimal F_JMJEHJ;
    /**
     * 应收金额合计
     */
    private BigDecimal F_YSJEHJ;
    /**
     * 税额合计
     */
    private BigDecimal F_SEHJ;
    /**
     * 不含税金额合计
     */
    private String F_BHSJEHJ;
    /**
     * 制单人编号
     */
    private String F_CUSER;
    /**
     * 制单人名称
     */
    private String F_CNAME;
    /**
     * 销售方式
     */
    private String F_FKFS;
    /**
     * 被红冲 ID
     */
    private String F_BHCID;

    /**
     * 明细
     */
    private List<T> dataDetail;

}
