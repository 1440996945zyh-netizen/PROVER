package com.yy.ppm.tallyExtrinsic.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.tallyExtrinsic.bean.dto.AppTallyCoilNumDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * App理货(TYardTallyPO)PO
 * @author chenfs
 * @date 2023-09-15
 */


@Getter
@Setter
@ToString
public class TYardTallyPO extends BasePO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 计划ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "计划ID不能为空")
    private Long planId;
    private Long noteId;
    private String truckNo;


    /**
     * 关联ID，理货ID用于记录是确认的哪条理货记录
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long relationId;

    /**
     * 航次ID
     */

    private Long shipvoyageId;

    private Long shipvoyageItemId;


    /**
     * 指令ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long trustId;
    private Long trustCargoInfoId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
    private String companyName;

    /**
     * 作业过程代码
     */
    private String processCode;

    /**
     * 作业过程名称
     */
    private String processName;

    /**
     * 作业过程源
     */
    private String source;


    private String tallyId;//(删除理货记录时用)

    /**
     * 作业过程目的
     */
    private String destination;

    /**
     * 作业过程更新场存节点（0不更新1理货2签票）
     */
    private Integer updatePoint;

    /**
     * 出入库标识
     */
    private Integer inoutYard;


    /**
     * 作业机械ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipmentId;

    /**
     * 作业机械编号
     */
    @NotBlank(message ="作业机械不能为空")
    private String equipmentNo;

    /**
     * 转运机械ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long transportEquipmentId;

    /**
     * 转运机械编号
     */
    private String transportEquipmentNo;


    /**
     * 总件数
     */
    private Integer quantity;


    /**
     * 总重量
     */
    private BigDecimal ton;

    /**
     * 备注
     */
    private String remark;

    /**
     * 子表
     */
    private List<TYardTallyItemPO> listTallyItemList;
    private List<SysFileDTO> mattachmentInfoList;
    private List<AppTallyCoilNumDTO> coilList; //标号列表
    private String coil; //标号

    /**
     * 录入时间
     */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private Date createTime;

    private String busType; //发货类型

    /**
     * 库场名称
     */
    private String storehouseName;

    /**
     * 库场ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long storehouseId;


    /**
     * 货位ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long locationId;

    /**
     * 货位编号
     */
    private String locationNo;

    /**
     * 垛位号
     */
    private String stackPosition;

    /**
     * 货物代码
     */
    private String cargoCode;

    /**
     * 货物名称
     */
    private String cargoName;

    /**
     * 票货ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cargoInfoId;

    /**
     * 起始库场ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private String storehouseIdSource;
    private String storehouseNameSource;

    /**
     * 终点库场ID
     */
    private String storehouseIdTarget;
    private String storehouseNameTarget;

    private Boolean isFrontType;
    private String workDate; //日期
    private String classCode; //班次
    private String className; //班次
    private String startDate;//开始时间
    private String endDate;//结束时间

    @JsonSerialize(using = ToStringSerializer.class)
    private Long operatorsId; //人员ID(作业机械)
    private String operatorsName; //人员姓名

    @JsonSerialize(using = ToStringSerializer.class)
    private Long transportOperatorsId; //人员ID(转运车)
    private String transportOperatorsName; //人员姓名

    //===新地磅参数
    private String workErWeiId;
    private String tsptId;
    private String planNo;

    /**
     * 磅单ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long  weighbridgeId;

    /**
     * 装卸班组
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;
    private String deptName;
    private String tallyStatus;
    private String isZd;
    private String isCrk;
    private String isFinished;
    private String weighOutDt;
}
