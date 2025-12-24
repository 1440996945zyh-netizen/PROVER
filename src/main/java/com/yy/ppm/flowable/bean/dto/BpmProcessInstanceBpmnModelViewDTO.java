package com.yy.ppm.flowable.bean.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 *  流程示例的 BPMN 视图
 */
@Data
@Accessors(chain = true)
public class BpmProcessInstanceBpmnModelViewDTO implements Serializable {

    // ========== 基本信息 ==========

    /**
     * 流程实例信息
     */
    private BpmProcessInstanceDTO processInstance;

    /**
     * 任务列表
     */
    private List<BpmTaskDTO> tasks;

    /**
     * BPMN XML
     */
    private String bpmnXml;

//    @Schema(description = "SIMPLE 模型")
//    private BpmSimpleModelNodeDTO simpleModel;

    // ========== 进度信息 ==========
    /**
     * 进行中的活动节点编号集合
     */
    private Set<String> unfinishedTaskActivityIds; // 只包括 UserTask

    /**
     * 已经完成的活动节点编号集合
     */
    private Set<String> finishedTaskActivityIds; // 包括 UserTask、Gateway 等，不包括 SequenceFlow

    /**
     * 已经完成的连线节点编号集合
     */
    private Set<String> finishedSequenceFlowActivityIds; // 只包括 SequenceFlow

    /**
     * 已经拒绝的活动节点编号集合
     */
    private Set<String> rejectedTaskActivityIds; // 只包括 UserTask

}
