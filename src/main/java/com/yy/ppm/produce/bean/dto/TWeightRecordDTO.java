package com.yy.ppm.produce.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.api.client.util.Lists;
import com.yy.framework.annotation.DateFormat;
import com.yy.ppm.business.bean.po.TBusVehicleTransferPO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;


@Setter
@Getter
public class TWeightRecordDTO {
    private Long id;
    //计划号
    private String planNo;
    //车牌号
    private String truckPlate;
    //身份证号
    private String idNumber;
    //一次磅时间
    @DateFormat(message = "预抵日期格式错误", value = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date weighInDt;
    //二次磅时间
    @DateFormat(message = "预抵日期格式错误", value = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date weighOutDt;
    private BigDecimal weightSelf;// 皮重
    private BigDecimal weightAll;// 毛重
    private BigDecimal weightGoods;// 净重
    private Integer carCount;// 趟数

    private String confirmByName;

}
