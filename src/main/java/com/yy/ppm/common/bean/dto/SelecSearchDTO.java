package com.yy.ppm.common.bean.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;


/**
 * @author FanQi
 * @version 1.0
 * @date 2023/5/15 11:37
 */
@Data
@ToString
public class SelecSearchDTO {

    /** 下拉框数据源类型 */
    private String type;
    /** 单选值 */
    private String valueContent;
    /** 多选值，多选用逗号隔开 */
    private List<String> valueContentList;
    /** 搜索内容 */
    private String labelContent;
    /** 默认显示多少条 */
    private Long number;
    /** 状态 */
    private String statusCode;

    Map<String, Object> param;

}
