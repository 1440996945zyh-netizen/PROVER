package com.yy.ppm.dispatch.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @ClassName 船舶昼夜计划(TDisShipDaynigttplan)SearchDTO
 * @author yy
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023年07月17日 10:31:00
 */
@Data
public class TDisShipDaynigttplanSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -22425403383420890L;

            /**主键ID*/
    private Long id;
            /**指令ID*/
    private Long trustId;
            /**计划日期*/
    private Date planDate;
            /**航次ID*/
    private Long shipvoyageId;
            /**航次子表*/
    private Long shipvoyageItemId;
            /**装卸,装、卸*/
    private String loadUnload;
            /**货种代码，多选*/
    private String cargoCategoryCode;
            /**货种名称，多选*/
    private String cargoCategoryName;
            /**载货量*/
    private BigDecimal loadNum;
            /**白班作业量*/
    private BigDecimal mornWorkNum;
            /**夜班作业量*/
    private BigDecimal nightWorkNum;
            /**结余量*/
    private BigDecimal residueNum;
            /**计划靠泊时间*/
    private Date berthingTimePlan;
            /**计划离泊时间*/
    private Date leaveBerthTimePlan;
            /**计划开工时间*/
    private Date starttimePlan;
            /**计划完工时间*/
    private Date endtimePlan;
            /**计划移泊时间*/
    private Date moveBerthTimePlan;
            /**计划移入泊位ID*/
    private Long moveInBerthId;
            /**计划移入泊位name*/
    private String moveInBerthName;
            /**泊位id*/
    private Long berthId;
            /**泊位name*/
    private String berthName;
            /**创建者-ID*/
    private Long createBy;
            /**创建者-姓名*/
    private String createByName;
                            /**更新者-姓名*/
    private String updateByName;
            }

