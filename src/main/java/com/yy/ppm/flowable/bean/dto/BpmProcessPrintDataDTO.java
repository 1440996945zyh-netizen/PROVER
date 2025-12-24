package com.yy.ppm.flowable.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 流程实例的打印数据
 */
@Data
public class BpmProcessPrintDataDTO implements Serializable {
    /**
     * 流程实例数据
     */
    private BpmProcessInstanceDTO processInstance;
    /**
     * 是否开启自定义打印模板
     */
    private Boolean printTemplateEnable;
    /**
     * 自定义打印模板 HTML
     */
    private String printTemplateHtml;
    /**
     * 审批任务列表
     */
    private List<Task> tasks;

    /**
     * 流程任务
     */
    @Data
    public static class Task {
        /**
         * 流程任务的编号
         */
        private String id;
        /**
         * 任务名称
         */
        private String name;
        /**
         * 签名 URL
         */
        private String signPicUrl;
        /**
         * 任务描述
         */
        private String description; // 该字段由后端拼接

    }

}
