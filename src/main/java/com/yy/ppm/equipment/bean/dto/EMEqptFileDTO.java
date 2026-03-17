package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMEqptFilePO;
import lombok.Data;

import java.util.List;

/**
 * 设备资料文件DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMEqptFileDTO extends EMEqptFilePO {

    private static final long serialVersionUID = 1L;

    /**
     * 设备名称（关联查询）
     */
    private String equipName;

    /**
     * 设备小类名称（关联查询）
     */
    private String equipSmallCategoryName;

    /**
     * 规格（关联查询）
     */
    private String specificCode;

    /**
     * 型号（关联查询）
     */
    private String modelNumber;

    /**
     * 制造厂家（关联查询）
     */
    private String manufacturer;

    /**
     * 资料名称（关联SYS_FILE表，通过FILE_TABLE_ID）
     */
    private String fileTableName;

    /**
     * 资料类型（关联SYS_FILE表，通过FILE_TABLE_ID）
     */
    private String fileTableType;

    /**
     * 附件ID列表
     */
    private List<Long> fileIds;
}

