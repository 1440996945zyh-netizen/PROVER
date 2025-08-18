package com.yy.ppm.statement.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

@Data
public class MiscSearchDTO extends PageParameter {
    private String shipvoyageItemId;
    private String customerId;
    private String routeType;
    private String status;
    private String processCode;
    private String rateItemCode;
    private String statementNo;
    private String shipName;
    private String voyage;

    }
