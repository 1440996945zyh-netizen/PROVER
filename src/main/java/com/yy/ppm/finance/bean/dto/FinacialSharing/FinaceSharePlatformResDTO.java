package com.yy.ppm.finance.bean.dto.FinacialSharing;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class FinaceSharePlatformResDTO {
    private String operId;
    private String businessType;
    private String operUserName;
    private String operUrl;
    private String operIp;
    private String status;
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operTime;
    private String operParam;
    private String jsonResult;
    private String errorMsg;
    private String operUserId;
    private String costTime;
    private Long invoiceId;

    private String sysInvoiceCode;
    private String companyName;
    private String customerName;
    private String invoiceAmount;

}
