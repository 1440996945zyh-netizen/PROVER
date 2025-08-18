package com.yy.ppm.finance.bean.dto;


import com.yy.ppm.finance.bean.po.TFdInvoiceDetailPO;
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
public class TFdInvoiceDTO extends TFdInvoicePO {

    private static final long serialVersionUID = 247146718870044966L;

    private BigDecimal tmpAmount;
    private String shipNameVoyage;

    /**
     * 文件列表
     */
    private List<Long> fileIds;

    private List<TFdInvoiceDetailDTO> statementList;
    //统计发票金额
    private BigDecimal countAmount;
    //条数信息
    private int total;

    /**
     * 红冲状态
     */
    private String redStatus;

    /**
     * 红冲状态
     */
    private String redStatusLabel;
    //是否开具付款收据
    private String isDebtorpayment;
}
