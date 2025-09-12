package com.yy.ppm.master.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
@Getter
@Setter
@ToString

public class MPrintPO  extends BasePO implements Serializable {
    private static final long serialVersionUID = -1101726927330759376L;

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
