package com.yy.ppm.business.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @ClassName 费率(TBusRate)SearchDTO
 * @author yy
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023年07月03日 16:48:00
 */
@Data
public class TBusRateSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 277379047681020924L;

            /**ID*/
    private Long id;
            /**费目ID*/
    private String rateItemCode;
            /**费目名称*/
    private String rateItemName;
            /**服务内容id*/
    private Long serviceContentId;
            /**服务内容名称*/
    private String serviceContentName;
            /**作业过程CODE*/
    private String processCode;
            /**作业过程NAME*/
    private String processName;
            /**货物代码*/
    private String cargoCode;
            /**货物名称*/
    private String cargoName;
            /**内外贸*/
    private String inteFore;
            /**费率值*/
    private BigDecimal rate;
            /**税率*/
    private BigDecimal taxRate;
            /**计量单位代码（字典：MEASUREMENT_UNIT）*/
    private String measurementUnitCode1;
            /**计量单位代码2*/
    private String measurementUnitCode2;
            /**0停用 1审核中 9未通过 10通过*/
    private String status;
            /**有效期起*/
    private Date startDate;
            /**有效期止*/
    private Date endDate;
            /**备注*/
    private String remark;
            /**创建者-ID*/
    private Long createBy;
            /**创建者-姓名*/
    private String createByName;
                            /**更新者-姓名*/
    private String updateByName;
    /**
     * 数据来源
     */
    private Integer dataSource;
    /**
     * 当前时间
     */
    private String currTime;
 }

