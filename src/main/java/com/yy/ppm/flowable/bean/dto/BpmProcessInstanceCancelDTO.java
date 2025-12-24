package com.yy.ppm.flowable.bean.dto;

import lombok.Data;

/**
 *  流程实例的取消
 */
@Data
public class BpmProcessInstanceCancelDTO {

    /**
     * 流程实例的编号
     */
    private String id;

    /**
     * 取消原因
     */
    private String reason;

}
