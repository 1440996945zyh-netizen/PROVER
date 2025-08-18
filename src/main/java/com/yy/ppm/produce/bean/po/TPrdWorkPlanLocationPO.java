package com.yy.ppm.produce.bean.po;


import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName 作业计划位置表(TPrdWorkPlanLocation)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月21日 16:22:00
 */
@Data
public class TPrdWorkPlanLocationPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -29211628643119181L;

    /** 主键ID */
    private Long id;
    /** 计划ID */
    private Long workPlanId;
    /** 方向(1源，2目标) */
    private String direction;
    /** 库场ID */
    private String storehouseId;
    /** 库场名称 */
    private String storehouseName;
    /** 区域ID */
    private String regionId;
    /** 区域名称 */
    private String regionName;
    /** 垛位ID */
    private String massId;
    /** 垛位名称 */
    private String massName;

}

