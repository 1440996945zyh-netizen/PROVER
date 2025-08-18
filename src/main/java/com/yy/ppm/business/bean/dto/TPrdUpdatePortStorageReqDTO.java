package com.yy.ppm.business.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

@Data
public class TPrdUpdatePortStorageReqDTO extends PageParameter {

    private String noteId;
    private String startDate;
    private String endDate;
    private String status;
    private String unionNo;
    private String cargoName;
    private String truckPlate;
    private String cargoInfoNo;

}
