package com.yy.ppm.flowable.bean.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.yy.common.page.PageParameter;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 流程用表单分页
 */
@Data
public class BpmFormSearchDTO extends PageParameter implements Serializable {
    /**
     * 表单名
     */
    private String name;
    /**
     * 状态
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
