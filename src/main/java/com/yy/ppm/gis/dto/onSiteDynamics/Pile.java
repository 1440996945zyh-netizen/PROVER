package com.yy.ppm.gis.dto.onSiteDynamics;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author linqi
 * @Description
 * @Date 2023-06-06 17:23
 */
@Setter
@Getter
public class Pile implements Serializable {

    private Long id;
    private String bollardCode;
    private String bollardName;
    private Long berthId;
    /**
     * 经度
     */
    private Double lon;

    /**
     * 维度
     */
    private Double lat;
}
