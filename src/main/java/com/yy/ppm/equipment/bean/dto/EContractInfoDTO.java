package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EContractInfoPO;
import com.yy.ppm.equipment.bean.po.EMaintProjApplyPO;
import com.yy.ppm.equipment.bean.po.EMaintProjApplyQuotaPO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 维修项目申请DTO
 * @author system
 */
@Data
public class EContractInfoDTO extends EContractInfoPO implements Serializable {

    private static final long serialVersionUID = 1L;



    // ========== 扩展字段（适配你之前的查询需求） ==========

    private String contractTypeLable;

    /**
     * 单位名称
     */
    private String unit_name;

}
