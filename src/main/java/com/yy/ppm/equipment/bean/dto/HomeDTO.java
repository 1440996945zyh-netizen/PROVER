package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.equipment.bean.po.EMaintInfoPO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 设备维修派工信息 DTO
 *
 * @author system
 * @version 1.0.0
 */
@Data
public class HomeDTO extends BasePO {

    private static final long serialVersionUID = 1L;


    private Long id;


    private String startTime;

    private String endTime;
    private Integer tbs;
    private Integer jxz;
    private Integer ywc;
    private Integer sumTb;
    private Integer sumWc;


    private String createDate;

    private String wcl;

}
