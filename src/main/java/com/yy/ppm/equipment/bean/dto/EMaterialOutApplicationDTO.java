package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaterialOutApplicationPO;
import lombok.Data;

import java.util.List;

/**
 * 物资出库申请DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialOutApplicationDTO extends EMaterialOutApplicationPO {

    private static final long serialVersionUID = 1L;

    /**
     * 明细列表
     */
    private List<EMaterialOutApplicationDetailDTO> detailList;

    private String processStatusLabel;
    private String processStatus;
    private String procInstId;

}

