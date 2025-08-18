package com.yy.ppm.business.bean.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class BusTrustDTO {

    /**
     * 计划号	Y
     */
    private String planNo;

    /**
     * 子计划号	Y
     */
    private String subPlanNo;

    /**
     * 英文船名	Y
     */
    private String vesselNameEn;

    /**
     * 中文船名	Y
     */
    private String vesselNameCn;

    /**
     * 进口航次	Y
     */
    private String voyageImport;

    /**
     * 业务类型	Y	提货：SG  集港/存:JG
     */
    private String billType;

    /**
     * 企业代码（货主）	Y
     */
    private String companyCode;
    private String companyName;
    private String consignorName;
    private String consignorCode;
    /**
     * 物流公司
     */
    private String consigneeCode;
    private String consigneeName;

    /**
     * 货物代码	Y
     */
    private String cargoCode;

    /**
     * 货物名称	Y
     */
    private String cargoName;

    /**
     * 计划场地	Y	062C08/103,062C08/111,062C11/103
     */
    private String planYard;
    /**
     * 计划场地	Y	062C08/103,062C08/111,062C11/103
     */
    private String nameYard;

    /**
     * 规格	Y
     */
    private String specs;

    /**
     * 包装	Y	0散货 1件货
     */
    private String packing;

    /**
     * 计划件数	Y	packing=0时,只显示’/’
     */
    private String planQuantity;

    /**
     * 已完成件数	Y	packing=0时,只显示’/’
     */
    private String finishQuantity;

    /**
     * 剩余件数	Y	packing=0时,只显示’/’
     */
    private String surplusQuantity;

    /**
     * 计划重量(吨)	Y	packing=1时,只显示’/’
     */
    private String planWeight;

    /**
     * 已完成量(吨)	Y	packing=1时,只显示’/’
     */
    private String finishWeight;

    /**
     * 剩余量(吨)	Y	packing=1时,只显示’/’
     */
    private String surplusWeight;


    /**
     * 委托件数
     */
    private String entrustQuantity;
    /**
     * 委托重量
     */
    private String entrustWeight;

    /**
     * 计划开始时间	Y
     */
    private String planStartTime;

    /**
     * 计划结束时间	Y
     */
    private String planEndTime;

    /**
     * 预约数量	Y
     */
    private String orderQuantity;

    /**
     * 是否过磅	Y
     */
    private String isWeigh;

    /**
     * 已停止车辆	Y
     */
    private String truckStop;

    /**
     * 在港车辆	Y
     */
    private String truckInPort;

    /**
     * 货物信息	Y	Array
     */
    private List<Cargo> cargoList;


    @Data
    public static class Cargo{

        /**
         * 规格
         */
        private String specs;

        /**
         * 件数
         */
        private String quantity;

        /**
         * 总重量(吨)
         */
        private String totalWeight;

    }

}
