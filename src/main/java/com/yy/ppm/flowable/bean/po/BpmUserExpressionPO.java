package com.yy.ppm.flowable.bean.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * BPM 用户常用审批语 PO
 */
@Data
public class BpmUserExpressionPO  extends BasePO implements Serializable {


    /**
     *  快捷语编号
     */
    private Long id;

    /**
     *  用户编号（为NULL时表示系统全局配置）
     */
    private Long userId;

    /**
     * 快捷语内容
     */
    private String content;

    /**
     * 类型快捷语类型（通过-APPROVE  通用-COMMON  拒绝-REJECT  转办-TRANSFER  委派-DELEGATE
     * 加签-ADD_SIGN   退回-RETURN   抄送-COPY）
     */
    private String expressionType;

    /**
     * 快捷语类型名称
     */
    private String expressionTypeName;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（0-开启，1-禁用）
     */
    private Integer status;


}
