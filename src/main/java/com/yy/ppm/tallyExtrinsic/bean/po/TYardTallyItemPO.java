package com.yy.ppm.tallyExtrinsic.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * App理货(TYardTallyItemPO)PO
 * @author chenfs
 * @date 2023-09-15
 */

@Getter
@Setter
@ToString
public class TYardTallyItemPO extends BasePO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 理货表ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tallyId;



    /**
     * 舱口
     */
    private String cabinNo;

    /**
     * 票货ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cargoInfoId;

    /**
     * 作业机械ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipmentId;

    /**
     * 作业机械编号
     */
    private String equipmentNo;

    /**
     * 货物代码
     */
    private String cargoCode;

    /**
     * 货物名称
     */
    private String cargoName;

    /**
     * 件数
     */
    @NotNull(message = "件数不能为空")
    private Integer quantity;


    /**
     * 重量
     */
    @NotNull(message = "重量不能为空")
    private BigDecimal ton;
    private Long trustCargoInfoId;

    /**
     * 库场ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long storehouseId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long storehouseTargetId;

    /**
     * 库场名称
     */
    private String storehouseName;
    private String storehouseTargetName;

    /**
     * 货位ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long locationId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long locationTargetId;

    /**
     * 货位编号
     */
    private String locationNo;
    private String locationTargetNo;

    /**
     * 垛位号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long stackPositionId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long stackPositionTargetId;
    private String stackPositionName;
    private String stackPositionTargetName;


    /**
     * 备注
     */
    private String remark;

    /*** 剩余件数*/
    private Double quantitySurplus;
    /*** 出库件数*/
    private Integer quantityOut;

    /*** 剩余重量*/
    private BigDecimal tonSurplus;
    /*** 出库重量*/
    private BigDecimal tonOut;
    private Double theoryTon; //理重
    /**
     * 装船清单id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long loadingListId;

    /**
     * 出入库id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long inOutId;

    /**
     * 录入时间
     */
    @JsonFormat(pattern = "yy-MM-dd HH:mm",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yy-MM-dd HH:mm")
    private Date createTime;

    /**
     * 录入时间
     */
    @JsonFormat(pattern = "yy-MM-dd HH:mm",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yy-MM-dd HH:mm")
    private Date createTimeH;

    private String createByNameH;

    private String tallyStatusName;

    /**
     * 转运机械ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long transportEquipmentId;

    /**
     * 转运机械编号
     */
    private String transportEquipmentNo;

    //目的场地
    private String storehouseIdSource;
    private String storehouseNameSource;

    //计划中 目的堆场
    private String storehouseIdTargetPlan;
    private String storehouseNameTargetPlan;
    private String processCode;
    private String processName;
    //源
    private String source;
    private String destination;

    private String planId; //计划id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long operatorsId; //人员ID(作业机械)
    private String operatorsName; //人员姓名
    private List<SysFileDTO> mattachmentInfoList;
    private String classCode;
    private String className;
    @JsonFormat(pattern = "yy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yy-MM-dd")
    private Date workDate;

}


