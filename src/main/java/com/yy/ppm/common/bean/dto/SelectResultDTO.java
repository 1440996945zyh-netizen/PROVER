package com.yy.ppm.common.bean.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 下拉框用PO
 *
 * @author rever
 */
@Getter
@Setter
public class SelectResultDTO implements Serializable {

    /**
     * 值
     */
    private String code;

    /**
     * 显示
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

}
