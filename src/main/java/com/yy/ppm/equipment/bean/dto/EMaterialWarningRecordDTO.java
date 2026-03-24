package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaterialWarningRecordPO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 预警消息DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EMaterialWarningRecordDTO extends EMaterialWarningRecordPO {

    private static final long serialVersionUID = 1L;

    /**
     * 处理状态文本
     */
    private String handleStatusLabel;

    /**
     * 处理人姓名
     */
    private String handleUserName;

    /**
     * 批量处理用
     */
    private List<Long> ids;
}