package com.yy.ppm.businessKH.vo.req;
/**
 * @ClassName FhksCargoInfoReqVo.java
 * @author lihuijie
 * @version 1.0.0
 * @Description 分货控数保存VO
 * @createTime 2022-05-04 14:35
 */

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class FhksCargoInfoReqVo  implements Serializable {
    private String note;//备注
    private String cargokey;//货健
    private BigDecimal fhks;//分货控数量
    private String userid;//



}
