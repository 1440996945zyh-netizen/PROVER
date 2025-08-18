package com.yy.ppm.dispatch.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 靠离计划表(TDisInoutPlan)PO
 *
 * @author 李振华
 * @date 2022-12-16 14:31:01
 */
@Getter
@Setter
@ToString
public class TDisInoutPlanPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 811170970654548526L;

    /**主键ID*/
    private Long id;
        /**航次ID*/
    private Long shipvoyageId;
        /**计划靠泊时间*/
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date berthTime;
        /**计划靠泊ID*/
    private Long berthId;
        /**计划靠泊泊位*/
    private String berthNo;
        /**计划开工时间*/
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date workStartTime;
        /**计划完工时间*/
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date workEndTime;
        /**计划移泊时间*/
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date moveBerthTime;
        /**计划移泊ID*/
    private Long moveBerthId;
        /**计划移泊泊位*/
    private String moveBerthNo;
        /**计划离泊时间*/
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date leaveBerthTime;
        /**备注*/
    private String remark;

}
