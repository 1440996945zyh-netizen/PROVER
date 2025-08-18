package com.yy.ppm.produce.bean.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DaoYunWeightGoodsDTO {
    private Long planId;
    private Long ticketId;
    private Long trustId;
    private BigDecimal weightGoods;
    private String trustType;
    private Date startTime;
    private Date endTime;
}
