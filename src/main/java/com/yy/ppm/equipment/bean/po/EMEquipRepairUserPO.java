package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * 外修单位合同实体
 *
 * @author zhuhao
 * @date 2020/7/22
 * @description 描述
 **/
@Data
public class EMEquipRepairUserPO extends BasePO implements Serializable {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 维修单位id
     */
    private Long repairContarctId;

    /**
     * 维修人名
     */
    private String repairName;

    /**
     * 身份证
     */
    private String idCard;

    /**
     * phone
     */
    private String phone;

}
