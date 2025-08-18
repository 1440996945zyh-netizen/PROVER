package com.yy.ppm.master.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;

@Data
public class MHqCargoPO extends BasePO implements Serializable {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 货名
     */
    private String cargoName;
}
