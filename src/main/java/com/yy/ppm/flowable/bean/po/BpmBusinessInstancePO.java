package com.yy.ppm.flowable.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 业务与流程实例信息
 */
@Data
public class BpmBusinessInstancePO extends BasePO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 业务具体单据的主键ID
     */
    private Long businessDataId;

    /**
     * 关联业务配置ID
     */
    private Long businessId;

    /**
     * 流程实例ID
     */
    private String procInstId;

    /**
     * 流程定义ID
     */
    private String procDefId;

    /**
     * 当前正在审批的节点名称
     */
    private String currentNodeName;

    /**
     * 当前节点的可处理人姓名（多人时用逗号分隔）
     */
    private String approverNames;

    /**
     * 流程状态（1:审批中, 2:审批通过, 3:审批驳回, 4:已撤回/作废）
     */
    private String instanceStatus;

    /**
     * 流程发起/启动的时间
     */
    private Date startTime;

    /**
     * 流程结束/归档的时间
     */
    private Date endTime;

}
