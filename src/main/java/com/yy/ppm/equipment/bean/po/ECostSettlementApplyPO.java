package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 结算申请表 PO
 *
 * @author fanxianjin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ECostSettlementApplyPO extends BasePO {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 结算单号(JS-YYYTMMDD-顺序号) */
    private String settlementNo;

    /** 维修单位ID */
    private Long maintOrgId;


    /** 所属单位ID */
    private Long useCompanyId;

    /** 承修单位名称 */
    private String maintOrgName;
    private String useCompanyName;

    /** 项目类型(定额/非定额) */
    private String projectType;

    /** 预算金额合计(元，含税) */
    private BigDecimal totalBudgetAmount;

    /** 实际金额合计(元，含税) */
    private BigDecimal totalActualAmount;

    /** 申请人ID */
    private Long applyUserId;

    /** 申请人名称 */
    private String applyUserName;

    /** 申请时间(年月日时分秒) */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyTime;

    /** 备注 */
    private String remark;
}
