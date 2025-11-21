package com.yy.ppm.flowable.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程定义
 */
@Getter
@Setter
@ToString
public class FlowProcDefDto extends PageParameter implements Serializable {
    /**
     * 流程id
     */
    private String id;
    /**
     * 流程名称
     */
    private String name;
    /**
     * 流程key
     */
    private String key;
    /**
     * 流程分类
     */
    private String category;
    /**
     * 配置表单名称
     */
    private String formName;
    /**
     * 配置表单id
     */
    private String formId;
    /**
     * 版本
     */
    private int version;
    /**
     * 部署ID
     */
    private String deploymentId;
    /**
     * 流程定义状态: 1:激活 , 2:中止
     */
    private int suspensionState;
    /**
     * 部署时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date deploymentTime;
}
