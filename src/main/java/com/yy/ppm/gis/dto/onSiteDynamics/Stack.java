package com.yy.ppm.gis.dto.onSiteDynamics;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author linqi
 * @Description
 * @Date 2023-06-06 17:23
 */
@Setter
@Getter
public class Stack implements Serializable {

    /**
     * 垛位id
     */
    private Long id;

    /**
     * 库场code
     */
    private String storageCode;

    /**
     * 垛位code
     */
    private String stackCode;

    /**
     * 垛位名字
     */
    private String stackName;

    /**
     * 库场id
     */
    private Long storageId;

    /**
     * 库场名字
     */
    private String storageName;

    /**
     * 库场类型code
     */
    private String storageTypeCode;

    /**
     * 库场类型名称
     */
    private String storageTypeName;

    /**
     * 垛位坐标
     */
    private List<StackCoordinate> coordinates;

    /**
     * 货物颜色
     */
    private String cargoColor;

    /**
     * 票货信息
     */
    private String cargoInfos;

    /**
     * 跑垛人
     */
    private String createByName;

    /**
     * 跑垛时间
     */
    private String positionTime;

    private String sideLength;

    private BigDecimal area;
    
    private String workAreaCd;
    
    private Integer carCount;
    
    private String macCode;

    @Setter
    @Getter
    public static class StackCoordinate {

        private Long id;

        private Integer seqNo;

        private Double lon;

        private Double lat;
    }
}
