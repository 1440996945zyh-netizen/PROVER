package com.yy.ppm.flowable.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
@Data
public class BpmBusinessConfigSearchDTO extends PageParameter implements Serializable {
    /**
     * 业务ID
     */
    private Long businessId;

    /**
     * 业务名称（模糊查询）
     */
    private String businessName;

    /**
     * 业务类型编码
     */
    private String businessTypeCode;

    /**
     * 业务类型名称（模糊查询）
     */
    private String businessTypeName;

    /**
     * 流程模型ID
     */
    private String procModelId;

    /**
     * 流程模型名称（模糊查询）
     */
    private String procModelName;

    /**
     * 流程定义KEY
     */
    private String procDefId;

    /**
     * 状态
     */
    private String status;
}
