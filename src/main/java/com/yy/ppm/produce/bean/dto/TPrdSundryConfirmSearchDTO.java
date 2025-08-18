package com.yy.ppm.produce.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
@Data
public class TPrdSundryConfirmSearchDTO extends PageParameter implements Serializable {

    private String truckPlate;
}
