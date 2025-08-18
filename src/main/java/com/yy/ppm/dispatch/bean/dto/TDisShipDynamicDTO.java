package com.yy.ppm.dispatch.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 船舶动态表(TDisShipDynamic)PO
 *
 * @author 李振华
 * @date 2022-12-16 13:48:55
 */
@Getter
@Setter
@ToString
public class TDisShipDynamicDTO extends BasePO implements Serializable {

    private static final long serialVersionUID = -40139011059429814L;

    /**主键ID*/
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
        /**航次ID*/
    @JsonSerialize(using = ToStringSerializer.class)
    private Long shipvoyageId;
        /**动态类型代码（字典）*/
    private String dynamicTypeCode;
        /**动态类型名称（字典）*/
    private String dynamicTypeName;
        /**动态时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date dynamicTime;
        /**泊位ID*/
    @JsonSerialize(using = ToStringSerializer.class)
    private Long berthId;
        /**泊位编号*/
    private String berthNo;
        /**舷靠*/
    private String berthType;
        /**停工原因ID*/
    private Long stopId;
        /**停工原因名称*/
    private String stopName;
        /**备注*/
    private String remark;

    /**首榄编号*/
    private String bollardNoStart;
    /**尾榄编号*/
    private String bollardNoEnd;
    /**停时类型代码(字典，STOP_TYPE)*/
    private String stopTypeCode;
    /**停时类型名称(字典，STOP_TYPE)*/
    private String stopTypeName;

    /**停工时间*/
    private String stopTime;
    /*停工时长*/
    private BigDecimal stopHours;

}
