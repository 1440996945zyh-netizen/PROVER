package com.yy.ppm.master.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
@Data
public class MPrintSearchDTO extends PageParameter implements Serializable {
    /**
     * 主键ID*/
    private Long id;

    /**
     * 模板类型编码*/
    private String modelTypeCode;

    /**
     * 模板类型名称*/
    private String modelTypeName;

    /**
     * 模板名称*/
    private String modelName;
    /**
     *模板JSON */
    private String modelContent;
}
