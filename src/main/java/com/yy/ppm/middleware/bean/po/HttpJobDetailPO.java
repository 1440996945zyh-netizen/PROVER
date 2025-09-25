package com.yy.ppm.middleware.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class HttpJobDetailPO extends BasePO implements Serializable {
    private Long id;

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
}
