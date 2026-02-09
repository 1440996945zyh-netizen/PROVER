package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 申报物资明细查询DTO（用于采购时选择）
 * @author system
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EMaterialApplicationDetailSearchDTO extends PageParameter {

    /** 申请单号 */
    private String applicationNo;

    /** 申报主题 */
    private String applicationTitle;

    /** 物资名称 */
    private String materialName;

    /** 物资代码 */
    private String materialCode;

    /** 状态（只查询已审批通过的，status=3） */
    private String status = "3";

    /** 开始日期（申请时间） */
    private String startDate;

    /** 结束日期（申请时间） */
    private String endDate;

    /** 采购状态（0-待采购，1-已采购） */
    private String purchaseStatus;

    /** 定点服务类别编码 */
    private String fixedServiceCategoryCode;

    /** 采购类型编码（01-比价，02-招标，03-定点服务） */
    private String purchaseTypeCode;
}

