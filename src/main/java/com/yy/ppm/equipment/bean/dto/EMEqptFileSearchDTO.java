package com.yy.ppm.equipment.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;
import java.io.Serializable;

/**
 * 设备资料文件查询DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMEqptFileSearchDTO  extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    private Long equipId;

    /**
     * 设备名称（用于模糊查询）
     */
    private String equipName;

    /**
     * 资料类型CODE
     */
    private String dataTypeCode;

    /**
     * 资料类型NAME
     */
    private String dataTypeName;

    /**
     * 文件表ID
     */
    private Long fileTableId;

    /**
     * 资料名称（关联SYS_FILE表，用于模糊查询）
     */
    private String fileTableName;
}

