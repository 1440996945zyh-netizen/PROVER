package com.yy.ppm.statement.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ConfirmForMiscAndStorageDTO extends BasePO {
    private Long Id;
    //1 misc   2 堆存费
    private Long statementId;
    private List<Long> ids;

    /**
     * 回执确认文件
     */
    private List<Long> fileIds;
    private String receiptRemark;


    /**
     * 商务确认人相关信息
     */
    private Long confirmBy;
    private String confirmByName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date confirmTime;


    private String taxInvoiceCode;
    private String taxInvoiceName;
}
