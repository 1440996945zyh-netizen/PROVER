package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 设备维修派工信息批量更新DTO
 * @author system
 */
@Data
public class EMaintInfoBatchUpdateDTO extends BasePO {

    private static final long serialVersionUID = 1L;

    /**
     * ID列表
     */
    private List<Long> ids;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 作废备注
     */
    private String cancelRemark;
}

