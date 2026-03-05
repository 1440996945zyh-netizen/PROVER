package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaintInfoPO;
import lombok.Data;

/**
 * 设备维修派工信息DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaintInfoDTO extends EMaintInfoPO {

    private static final long serialVersionUID = 1L;

    /**
     * 设备名称（关联查询）
     */
    private String equipName;

    /**
     * 设备编码（关联查询）
     */
    private String equipCode;

    /**
     * 故障图片文件ID列表
     */
    private java.util.List<Long> faultImageIds;

    /**
     * 维修时长(小时)
     */
    private java.math.BigDecimal maintDuration;

    /**
     * 故障时长(小时)
     */
    private java.math.BigDecimal faultDuration;

    /**
     * 配件更换列表
     */
    private java.util.List<EMaintPartReplaceDTO> partReplaceList;

    /**
     * 设备小类名称
     */
    private String equipSmallCategoryName;

    /**
     * 使用部门名称
     */
    private String useOrgName;

}

