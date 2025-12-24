package com.yy.ppm.flowable.bean.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 流程用表单
 */
@Data
public class BpmFormPO extends BasePO implements Serializable {
    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 表单名
     */
    private String name;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 表单的配置
     */
    private String conf;
    /**
     * 表单项的数组 ？
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> fields;
    /**
     * 备注
     */
    private String remark;
}
