package com.yy.ppm.middleware.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
@Getter
@Setter
@ToString
public class HttpJobLogsSearchDTO extends PageParameter implements Serializable {
    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务分组
     */
    private String jobGroup;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 请求类型
     */
    private String requestType;

    /**
     * 请求地址Url
     */
    private String httpUrl;

    /**
     * 请求参数
     */
    private String httpParams;
    private String triggerState;
    private String searchParam;
}
