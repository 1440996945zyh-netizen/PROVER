package com.yy.ppm.gis.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TKeypointsPO extends BasePO implements Serializable {
    /**
     * 关键点主键ID
     */
    private Long pointId;

    private Long id;

    /**
     * 关键点名称
     */
    private String pointName;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 维度
     */
    private BigDecimal latitude;

    /**
     * 关键点类型 1路口 2入口 3出口
     */
    private Integer type;

    /**
     * 状态 1在用 0停用
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}

