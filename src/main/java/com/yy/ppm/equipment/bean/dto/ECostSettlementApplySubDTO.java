package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.ECostSettlementApplySubPO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 结算申请子表 DTO
 *
 * @author fanxianjin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ECostSettlementApplySubDTO extends ECostSettlementApplySubPO {

    private static final long serialVersionUID = 1L;

    /** 设备名称 */
    private String equipName;

    /** 派工类型名称 (项目类型) */
    private String dispatchTypeName;
}
