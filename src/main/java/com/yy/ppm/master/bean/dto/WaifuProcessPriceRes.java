package com.yy.ppm.master.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
@Data
public class WaifuProcessPriceRes extends PageParameter implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String waifuPackageCode;
    private String waifuPackageName;
    private String processCode;
    private String processName;
    private String processDetailCode;
    private String processDetailName;
    private Long deptId;
    private String deptName;
    private BigDecimal rate;
    private String remark;
    private String positionCode;
    private String positionName;
    private String allotType;
    private String machineTypeCode;
    private String machineTypeName;
}
