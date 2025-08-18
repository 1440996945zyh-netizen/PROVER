package com.yy.ppm.master.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

@Data
public class MHqCargoSearchDTO extends PageParameter implements Serializable {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 货名
     */
    private String cargoName;
}
