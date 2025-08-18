package com.yy.ppm.dispatch.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName 集疏港昼夜计划(TDisPortDaynightplan)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年11月14日 10:31:00
 */
@Data
public class TDisPortDaynightplanPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -61491625746487193L;

    /** 主键ID */
    private Long id;
    /** 指令ID */
    private Long trustId;
    /** 票货ID */
    private Long trustCargoId;
    /** 计划号 */
    private String businessNo;
    /** 航次id */
    private Long shipvoyageId;
    /** 航次子表id */
    private Long shipvoyageItemId;
    /** 船名航次 */
    private String shipvoyageName;
    /** SCN */
    private String scn;
    /** 货主id */
    private Long cargoOwnerId;
    /** 货主名称 */
    private String cargoOwnerName;
    /** 作业港区编码 */
    private String portCode;
    /** 作业港区 */
    private String portName;
    /** 货物编码 */
    private String cargoCode;
    /** 货名 */
    private String cargoName;
    /** 包装代码 */
    private Long packingCode;
    /** 包装名称 */
    private String packingName;
    /** 贸易类别（内外贸） */
    private String tradeType;
    /** 计划重量 */
    private BigDecimal planTon;
    /** 审核状态 */
    private String status;
    /** 位置 */
    private String massNamesTarget;
    /** 位置名称 */
    private String massNamesTargetLabel;
    /** 分段计划量-1800~2200 */
    private BigDecimal segPlanTon1;
    /** 分段计划量-2200~0200 */
    private BigDecimal segPlanTon2;
    /** 分段计划量-0200~0600 */
    private BigDecimal segPlanTon3;
    /** 分段计划量-0600~1000 */
    private BigDecimal segPlanTon4;
    /** 审核人 */
    private Long examineBy;
    /** 审核人名 */
    private String examineByName;
    /** 审核日期 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date examineTime;

    /** 计划日期 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date planDate;

    /** 通知单类型 */
    private String noticeType;

    /** 计划开始时间 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date beginTime;

    /** 计划结束时间 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endTime;
    /**
     * 计划类型（1 出入库；2 直取）
     */
    private String planType;
    /**
     * 班次
     */
    private String classCode;
}

