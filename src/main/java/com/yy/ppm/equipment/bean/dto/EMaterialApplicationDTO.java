package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaterialApplicationPO;
import lombok.Data;

import java.util.List;

/**
 * 物资申报DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialApplicationDTO extends EMaterialApplicationPO {

    private static final long serialVersionUID = 1L;

    /**
     * 明细列表
     */
    private List<EMaterialApplicationDetailDTO> detailList;


    private String processStatus;
    private String processStatusLabel;
    private String procInstId;
}

