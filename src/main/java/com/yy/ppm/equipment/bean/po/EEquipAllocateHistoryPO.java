package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 设备调拨历史记录PO
 * @author system
 */
@Getter
@Setter
@ToString
public class EEquipAllocateHistoryPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 工单id（对应调拨工单主键id）
     */
    private Long orderId;

    /**
     * 设备id
     */
    private Long equipId;

    /**
     * 调拨变更前历史记录（json形式存储）
     */
    private String lastChangeInfo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否删除（0.否，1.是）
     */
    private Long delFlag;

}
