package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: fanxianjin
 * @Desc: 设备报废历史PO
 * @Date: 2026/2/28 14:27
 */
@Getter
@Setter
@ToString
public class EEquipScrapHistoryPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 报废申请ID
     */
    private Long orderId;

    /**
     * 设备ID
     */
    private Long equipId;

    /**
     * 变更历史记录（JSON数据）
     */
    private String lastChangeInfo;

    /**
     * 是否删除（0否，1是）
     */
    private Long delFlag;

}
