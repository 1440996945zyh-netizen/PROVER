package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 * 预警消息查询
 */
@Data
public class EMaterialWarningRecordSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 处理状态
     * 0-未处理 1-已处理
     */
    private String handleStatus;
}