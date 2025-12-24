package com.yy.ppm.flowable.bean.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.yy.common.page.PageParameter;
import lombok.Data;
import lombok.experimental.Accessors;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;

import java.io.Serializable;
import java.util.List;

/**
 * BPM 流程定义的拓信息
 * 主要解决 Flowable {@link ProcessDefinition} 不支持拓展字段，所以新建该表
 *
 */
@Data
@Accessors(chain = true)
public class BpmProcessDefinitionInfoSearchDTO extends PageParameter implements Serializable {
    /**
     * 流程名称
     */
    private String name;
    /**
     * 流程分类
     */
    private String category;
    /**
     * 流程状态
     */
    private Integer status;

    /**
     * 标识-精准匹配
     */
    private String key;

}
