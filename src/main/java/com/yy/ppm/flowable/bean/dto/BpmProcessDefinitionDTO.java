package com.yy.ppm.flowable.bean.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
/**
 * 管理后台 - 流程定义 Response VO
 */
@Data
@Accessors(chain = true)
public class BpmProcessDefinitionDTO extends BpmModelMetaInfoDTO implements Serializable {
    /**
     * 编号，必传，示例：1024
     */
    private String id;

    /**
     * 版本，必传，示例：1
     */
    private Integer version;

    /**
     * 流程名称，必传，示例：芋道
     */
    private String name;

    /**
     * 流程标识，必传，示例：youdao
     */
    private String key;

    /**
     * 流程分类，示例：1
     */
    private String category;

    /**
     * 流程分类名字，示例：请假
     */
    private String categoryName;

    /**
     * 流程模型的类型，必传，参考BpmModelTypeEnum枚举类，示例：10
     */
    private Integer modelType;

    /**
     * 流程模型的编号，必传，示例：ABC
     */
    private String modelId;

    /**
     * 表单的配置（JSON字符串），在表单类型为BpmModelFormTypeEnum.CUSTOM时必须非空，必传
     */
    private String formConf;

    /**
     * 表单项的数组（JSON字符串的数组），在表单类型为BpmModelFormTypeEnum.CUSTOM时必须非空，必传
     */
    private List<String> formFields;

    /**
     * 表单名字，示例：请假表单
     */
    private String formName;

    /**
     * 中断状态，必传，参考SuspensionState枚举，示例：1
     */
    private Integer suspensionState;

    /**
     * 部署时间，非必须返回，从对应的Deployment中读取
     */
    private Date deploymentTime;

    /**
     * BPMN XML内容，非必须返回，从对应的BpmnModel中读取
     */
    private String bpmnXml;

    /**
     * SIMPLE设计器模型数据（json格式），非必须返回
     */
    private String simpleModel;

    /**
     * 流程定义排序，必传，示例：1024
     */
    private Long sort;

    /**
     * BPMN UserTask 用户任务
     */
    @Data
    public static class UserTask {

        /**
         * 任务标识，必传，示例：sudo
         */
        private String id;

        /**
         * 任务名，必传，示例：王五
         */
        private String name;

    }
}
