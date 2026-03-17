package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 结算申请查询 DTO
 *
 * @author fanxianjin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ECostSettlementApplySearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 结算单号 */
    private String settlementNo;

    /** 承修单位名称 */
    private String maintOrgName;

    /** 承修单位ID */
    private Long maintOrgId;

    /** 项目类型 */
    private String projectType;

    /** 申请人名称 */
    private String applyUserName;

    /** 工单号 */
    private String workOrderNo;
}
