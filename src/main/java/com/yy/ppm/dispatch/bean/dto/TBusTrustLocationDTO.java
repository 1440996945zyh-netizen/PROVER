package com.yy.ppm.dispatch.bean.dto;


import com.yy.ppm.business.bean.dto.TBusTrustCargoDTO;
import com.yy.ppm.dispatch.bean.po.TBusTrustLocationPO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanLocationDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName 集疏港作业通知单位置表，传输渤海通使用(TBusTrustLocation)DTO
 * @author makejava
 * @version 1.0.0
 * @Description
 * @createTime 2023年09月27日 14:34:00
 */
@Data
public class TBusTrustLocationDTO extends TBusTrustLocationPO {

    private static final long serialVersionUID = 707008518838646909L;
	
	private Integer status;//0代表删除，1代表新增，2代表更新

    /** 场区 */
    List<Location> locationListTarget;

    /** 回显用名称拼接 */
    String massNamesTarget;

    /** 回显用ids */
    List<String> regionIdsTarget;

    /** 回显指令货物 */
    List<TBusTrustCargoDTO> cargoList;

    @Getter
    @Setter
    public static class Location{
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

}
