package com.yy.ppm.businessKH.vo.req;
/**
 * @ClassName CargoInfoSearchReqVo.java
 * @author lihuijie
 * @version 1.0.0
 * @Description 控货管理查询入参VO
 * @createTime 2022-05-04 14:35
 */


import com.yy.ppm.businessKH.model.CargoInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PartCargoInfoReqVo extends CargoInfo implements Serializable {
    private String hqcyrid2;//海关放行人id
    private BigDecimal fhl;//分货量


}
