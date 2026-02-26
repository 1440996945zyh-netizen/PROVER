package com.yy.ppm.flowable.bean.dto;

import lombok.Data;

import java.util.List;

/**
 * 流程表单字段 VO
 */
@Data
public class BpmFormFieldDTO {
    /**
     * 字段类型
     */
    private String type;
    /**
     * 字段标识
     */
    private String field;
    /**
     * 字段标题
     */
    private String title;
    /**
     * 子字段列表（用于支持嵌套容器）
     */
    private List<BpmFormFieldDTO> children;
}
