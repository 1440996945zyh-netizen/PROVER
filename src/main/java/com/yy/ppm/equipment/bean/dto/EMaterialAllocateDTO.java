package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaterialAllocatePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 物资调拨 DTO。
 * 用于调拨主单详情展示、编辑回显以及流程状态扩展字段返回。
 *
 * @author system
 */
@Getter
@Setter
@ToString
public class EMaterialAllocateDTO extends EMaterialAllocatePO {

    private static final long serialVersionUID = 1L;

    /**
     * 审批状态编码。
     */
    private String processStatus;

    /**
     * 审批状态名称。
     */
    private String processStatusLabel;

    /**
     * 流程实例ID。
     */
    private String procInstId;

    /**
     * 调拨明细列表。
     */
    private List<EMaterialAllocateDetailDTO> detailList;
}
