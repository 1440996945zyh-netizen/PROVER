package com.yy.ppm.finance.bean.dto;


import com.yy.ppm.finance.bean.po.TFdInvoiceDetailPO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 发票子表(TFdInvoiceDetail)DTO
 * @Description
 * @createTime 2023年09月15日 20:22:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TFdInvoiceDetailDTO extends TFdInvoiceDetailPO {

    private static final long serialVersionUID = -23558533163959101L;

    /**
     * 回显付款人信息
     */
    private String customerId;
    private String customerName;

    /**
     * 货物名称
     */
    private String cargoName;


    private Long statementId;
    private BigDecimal invoiceAmount = BigDecimal.ZERO;
    private BigDecimal invoiceNumber = BigDecimal.ZERO;
    //费用类型  1船方 2货方 3是杂项
    private Long feeType;
    private String shipNameVoyage;

    private String shipFeeType;

    /**
     * 发票新增查询结算单状态用
     */
    private String status;

    /**
     * 商品税收分类编码
     */
    private String productCode;



}
