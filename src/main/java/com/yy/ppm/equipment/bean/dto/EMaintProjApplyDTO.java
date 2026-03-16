package com.yy.ppm.equipment.bean.dto;

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
public class EMaintProjApplyDTO extends EMaintProjApplyPO implements Serializable {

    private static final long serialVersionUID = 1L;



    // ========== 扩展字段（适配你之前的查询需求） ==========
    /**
     * 申请单号+维修单位名称拼接字段（APP_NUMBER + 维修单位名称）
     */
    private String appUnitName;
    private String statusLable;

    private String processStatus;
    private String processStatusLable;


    List<EMaintProjApplyQuotaPO> list;
}
