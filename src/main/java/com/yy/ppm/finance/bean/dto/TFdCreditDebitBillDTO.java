package com.yy.ppm.finance.bean.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.finance.bean.po.TFdCreditDebitBillDetailPO;
import com.yy.ppm.finance.bean.po.TFdCreditDebitBillPO;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 贷方解放票据主表(TFdCreditDebitBill)DTO
 * @Description
 * @createTime 2023年10月08日 16:19:00
 */
@Data
public class TFdCreditDebitBillDTO extends TFdCreditDebitBillPO {

    private static final long serialVersionUID = -98264772817058863L;

    List<TFdCreditDebitBillDetailDTO> detailList;

    TFdCreditDebitBillDetailDTO detailDto;

    private String prepaymentTypeCode;

}
