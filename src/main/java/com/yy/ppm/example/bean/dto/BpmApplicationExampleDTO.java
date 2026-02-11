package com.yy.ppm.example.bean.dto;

import com.yy.ppm.example.bean.po.BpmApplicationExamplePO;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;
import lombok.Data;


import java.io.Serializable;

@Data
public class BpmApplicationExampleDTO extends BpmApplicationExamplePO implements Serializable {
    /**
     * 业务菜单ID
     */
    private Long businessId;

    /**
     * 流程发起的数据
     */
    private BpmProcessInstanceDTO bpmProcessInstanceDTO;

    /**
     * 状态label
     */
    private String approvalStatusLabel;

    /**
     * 当前正在审批的节点名称
     */
    private String currentNodeName;

    /**
     * 当前节点的可处理人姓名（多人时用逗号分隔）
     */
    private String approverNames;

    /**
     * 流程实例ID
     */
    private String procInstId;
    /**
     * 流程定义ID
     */
    private String procDefId;
}
