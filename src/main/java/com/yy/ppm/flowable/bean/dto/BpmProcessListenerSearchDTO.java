package com.yy.ppm.flowable.bean.dto;

import com.yy.common.page.PageParameter;
import com.yy.ppm.flowable.bean.po.BpmProcessListenerPO;
import lombok.Data;

import java.io.Serializable;

/**
 * BPM 流程监听器 DTO
 *
 */
@Data
public class BpmProcessListenerSearchDTO extends PageParameter implements Serializable {
    /**
     * 监听器名字
     */
    private String listenerName;

    /**
     * 监听器状态(0开启1关闭)
     */
    private Integer listenerStatus;

    /**
     * 监听器类型编码
     */
    private String listenerTypeCode;

    /**
     * 监听器类型名称
     */
    private String listenerTypeName;

    /**
     * 监听器事件编码
     */
    private String listenerEventCode;

    /**
     * 监听器事件名称
     */
    private String listenerEventName;

    /**
     * 监听器值类型编码
     */
    private String listenerValueTypeCode;

    /**
     * 监听器值类型名称
     */
    private String listenerValueTypeName;

    /**
     * 监听器值
     */
    private String listenerValue;
}
