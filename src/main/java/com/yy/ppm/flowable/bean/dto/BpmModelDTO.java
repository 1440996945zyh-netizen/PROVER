package com.yy.ppm.flowable.bean.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class BpmModelDTO extends BpmModelMetaInfoDTO implements Serializable {
    /**
     * 编号
     */
    private String id;

    /**
     * 流程标识
     */
    private String key;

    /**
     * 流程名称
     */
    private String name;

    /**
     * 流程图标
     */
    private String icon;

    /**
     * 流程分类编号
     */
    private String category;

    /**
     * 流程分类名字
     */
    private String categoryName;

    /**
     * 表单名字
     */
    private String formName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 可发起的用户数组
     */
    private List<UserSimpleBaseDTO> startUsers;

    /**
     * 可发起的部门数组
     */
    private List<DeptSimpleBaseDTO> startDepts;

    /**
     * BPMN XML
     */
    private String bpmnXml;

//    /**
//     * 仿钉钉流程设计模型对象
//     */
//    private BpmSimpleModelDTO simpleModel;

    /**
     * 最新部署的流程定义
     */
    private BpmProcessDefinitionDTO processDefinition;

    /**
     * 模型状态（停用启用）
     */
    private Integer state;
}
