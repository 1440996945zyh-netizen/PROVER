package com.yy.ppm.master.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 *
 */
@Data
public class MDictTypeSearchDTO extends PageParameter implements Serializable {

    /**
     * 类型CD*/
    private String dictType;
    /**
     * 类型名称*/
    private String dictName;
    /**
     * 是否开放*/
    private String isOpen;
    /**
     * 状态*/
    private String status;

}
