package com.yy.ppm.produce.bean.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName 车辆过磅记录DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2024年01月25日 13:09:00
 */
@Data
public class TPrdVehiclePoundDTO implements Serializable {

    private static final long serialVersionUID = 513228687940341075L;

    /**
     * 计划号
     */
    private String planNo;
    /**
     * 车牌号
     */
    private String truckPlate;
    /**
     * 任务号
     */
    private String tsptId;
    private List<String> tsptIdList;
    /**
     * 是否完成
     */
    private String isFinished;

    private Integer poundNum;

}
