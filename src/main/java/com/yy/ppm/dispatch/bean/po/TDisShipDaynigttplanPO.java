package com.yy.ppm.dispatch.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName 船舶昼夜计划(TDisShipDaynigttplan)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月17日 10:31:00
 */
@Data
public class TDisShipDaynigttplanPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -61491625746487193L;

    /** 主键ID */
    private Long id;
    /** 指令ID */
    private Long trustId;
    /** 计划日期 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date planDate;
    /** 航次ID */
    private Long shipvoyageId;
    /** 航次子表 */
    private Long shipvoyageItemId;
    /** 装卸,装、卸 */
    private String loadUnload;
    /** 货种代码，多选 */
    private String cargoCategoryCode;
    /** 货种名称，多选 */
    private String cargoCategoryName;
    /** 载货量 */
    private BigDecimal loadNum;
    /**白班作业量*/
    private BigDecimal mornWorkNum;
    /**夜班作业量*/
    private BigDecimal nightWorkNum;
    /** 结余量 */
    private BigDecimal residueNum;
    /** 计划靠泊时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date berthingTimePlan;
    /** 计划离泊时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date leaveBerthTimePlan;
    /** 计划开工时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date starttimePlan;
    /** 计划完工时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endtimePlan;
    /** 计划移泊时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date moveBerthTimePlan;
    /** 计划移入泊位ID */
    private Long moveInBerthId;
    /** 计划移入泊位name */
    private String moveInBerthName;
    /** 泊位id */
    private Long berthId;
    /** 泊位name */
    private String berthName;

}

