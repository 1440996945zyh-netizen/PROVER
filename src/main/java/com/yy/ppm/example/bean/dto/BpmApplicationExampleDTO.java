package com.yy.ppm.example.bean.dto;

import com.yy.ppm.example.bean.po.BpmApplicationExamplePO;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;
import lombok.Data;


import java.io.Serializable;

@Data
public class BpmApplicationExampleDTO extends BpmApplicationExamplePO implements Serializable {
    /**
     * 流程发起的数据
     */
    private BpmProcessInstanceDTO bpmProcessInstanceDTO;

    /**
     * 业务菜单ID
     */
    private Long businessId;

    /**
     * 状态label
     */
    private String approvalStatusLabel;
}
