package com.yy.ppm.produce.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @ClassName (TBusPlanEntrust)DTO
 * @author makejava
 * @version 1.0.0
 * @Description
 * @createTime 2024年01月05日 14:47:00
 */
@Data
public class TPrdPlanEntrustResultDTO extends BasePO implements Serializable {

    private static final long serialVersionUID = -57172243886403254L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 主键ID
     */
    private Long trustCargoId;
    /**
     * 计划号
     */
    private String planNo;
    /**
     * 计划号
     */
    private String subPlanNo;
    /**
     * 船名
     */
    private String shipName;
    /**
     * 航次
     */
    private String voyage;
    /**
     * 货主
     */
    private Long cargoOwnerId;
    private String cargoOwnerName;
    private String cargoOwnerCode;
    private String cargoOwnerTin;
    /**
     * 货名
     */
    private String cargoName;
    private String cargoCode;
    /**
     * 规格
     */
    private String specs;
    /**
     * 包装
     */
    private String packingName;
    private String packingCode;
    /**
     * 件数
     */
    private Integer quantity;
    /**
     * 作业位置
     */
    private String planYard;
    /**
     * 计划重量
     */
    private BigDecimal planTon;
    /**
     * 已完成重量
     */
    private BigDecimal finishedTon;
    /**
     * 剩余重量
     */
    private BigDecimal surplusTon;
    /**
     * 计划开始时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date planStartTime;
    /**
     * 计划结束时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date planEndTime;
    /**
     * 预约数量
     */
    private Integer orderNum;
    /**
     * 是否过磅
     */
    private String isWeigh;
    /**
     * 已停止车辆
     */
    private Integer truckStopNum;
    /**
     * 已停止车辆
     */
    private Integer truckInPortNum;
    /**
     * 船名航次
     */
    private String shipNameVoyage;
    /**
     * 计划时间
     */
    private String planTime;
    /**
     * 货物列表
     */
    private List<TPrdPlanEntrustCargoDTO> cargoList;

}
