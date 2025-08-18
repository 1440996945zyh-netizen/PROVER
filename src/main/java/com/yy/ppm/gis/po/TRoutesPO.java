package com.yy.ppm.gis.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;

@Data
public class TRoutesPO extends BasePO implements Serializable {
    /**
     * 主键
     */
    private Integer routeId;

    /**
     * 路线名称
     */
    private String routeName;

    /**
     * 路线起点ID
     */
    private Integer beginPid;

    /**
     * 路线终点ID
     */
    private Integer endPid;

    /**
     * 路线类型（0双行 1单行）
     */
    private Integer routeType;

    /**
     * 状态（1在用 0停用）
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}

