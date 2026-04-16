package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 物资调拨历史 PO。
 * 用于记录调拨执行前的快照信息，便于审计和追溯。
 *
 * @author system
 */
@Getter
@Setter
@ToString
public class EMaterialAllocateHistoryPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID。
     */
    private Long id;

    /**
     * 调拨主表ID。
     */
    private Long allocateId;

    /**
     * 调拨明细ID。
     */
    private Long allocateDetailId;

    /**
     * 物资ID。
     */
    private Long materialId;

    /**
     * 变更前快照信息。
     */
    private String lastChangeInfo;

    /**
     * 备注。
     */
    private String remark;
}
