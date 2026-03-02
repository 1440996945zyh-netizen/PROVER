package com.yy.ppm.flowable.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;



/**
 * BPM 用户常用审批语分页查询 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BpmUserExpressionSearchDTO extends PageParameter implements Serializable {

    /**
     * 快捷语内容
     */
    private String content;

    /**
     * 类型（COMMON_PHRASES_TYPE 常用审批语类型）
     * 快捷语类型（通过-APPROVE  通用-COMMON  拒绝-REJECT  转办-TRANSFER  委派-DELEGATE
     * 加签-ADD_SIGN   退回-RETURN   抄送-COPY）
     */
    private String expressionType;

     /**
      * 快捷语类型名称
      */
    private String expressionTypeName;

    /**
     *  用户编号（为NULL时表示系统全局配置）
     */
    private Long userId;

    /**
     * 状态（0-开启，1-禁用）
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;
}
