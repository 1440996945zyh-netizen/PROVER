package com.yy.ppm.finance.bean.dto.FinacialSharing;

import lombok.Data;

import java.util.Date;

@Data
public class SharePlatformLogDTO {
    private String operId;
    private String businessType;
    private String operUserName;
    private String operUrl;
    private String operIp;
    private String status;
    private Date   operTime;
    private String operParam;
    private String jsonResult;
    private String errorMsg;
    private String operUserId;
    private String costTime;
    private Long invoiceId;
}
