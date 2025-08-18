package com.yy.ppm.business.bean.dto;


import com.yy.ppm.business.bean.po.TBusRatePO;
import lombok.Data;

/**
 * @ClassName 费率(TBusRate)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月03日 16:48:00
 */
@Data
public class TBusRateDTO extends TBusRatePO {

    private static final long serialVersionUID = -74805729703512435L;

    private String rateType;

    private String routeType;

    private String rateItemAndRate;

    private String rateCodeEas;
    private String rateNameEas;
    private String easType;

}
