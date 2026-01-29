package com.yy.ppm.flowable.bean.po;

import com.yy.ppm.common.bean.po.BasePO;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description BPM业务配置PO
 */
@Getter
@Setter
@ToString
public class BpmBusinessConfigPO extends BasePO implements Serializable {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 业务ID
     */
    private Long businessId;

    /**
     * 业务名称
     */
    private String businessName;

    /**
     * 业务类型编码
     */
    private String businessTypeCode;

    /**
     * 业务类型名称
     */
    private String businessTypeName;

    /**
     * 流程模型ID
     */
    private String procModelId;

    /**
     * 流程模型名称
     */
    private String procModelName;

    /**
     * 流程定义KEY
     */
    private String procDefKey;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private String status ;


}