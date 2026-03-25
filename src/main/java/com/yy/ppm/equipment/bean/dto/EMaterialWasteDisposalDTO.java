package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaintProjApplyPO;
import com.yy.ppm.equipment.bean.po.EMaintProjApplyQuotaPO;
import com.yy.ppm.equipment.bean.po.EMaterialWasteDisposalPO;
import com.yy.ppm.equipment.bean.po.EMaterialWasteDisposalSubPO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 维修项目申请DTO
 * @author system
 */
@Data
public class EMaterialWasteDisposalDTO extends EMaterialWasteDisposalPO implements Serializable {

    private static final long serialVersionUID = 1L;



    // ========== 扩展字段（适配你之前的查询需求） ==========


    private String dictLabel;
    List<EMaterialWasteDisposalSubPO> list;
}
