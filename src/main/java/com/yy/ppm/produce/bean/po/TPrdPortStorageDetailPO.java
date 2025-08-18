package com.yy.ppm.produce.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * (TPrdPortStorageDetail)PO
 *
 * @author linqi
 * @since 2023-08-21 11:11:21
 */
@Setter
@Getter
public class TPrdPortStorageDetailPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 港存主表ID
     */
    private Long portStorageId;

    /**
     * 票货ID
     */
    @NotNull(message = "票货ID不能为空")
    private Long cargoInfoId;

    /**
     * 日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "日期不能为空")
    private Date workDate;

    /**
     * 班次code字典 WORK_SCHEDULE
     */
    @NotBlank(message = "班次编码不能为空")
    private String classCode;

    /**
     * 班次NAME
     */
    @NotBlank(message = "班次名称不能为空")
    private String className;

    /**
     * 子作业过程代码
     */
    private String processDetailCode;

    /**
     * 子作业过程名称
     */
    private String processDetailName;

    /**
     * 库场ID
     */
    @NotNull(message = "库场ID不能为空")
    private Long storehouseId;

    /**
     * 库场名称
     */
    private String storehouseName;

    /**
     * 区域ID
     */
    @NotNull(message = "区域ID不能为空")
    private Long regionId;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 垛位ID
     */
    @NotNull(message = "垛位ID不能为空")
    private Long massId;

    /**
     * 垛位名称
     */
    private String massName;

    /**
     * 件数
     */
    private Integer quantity;

    /**
     * 吨数(数量)
     */
    @NotNull(message = "吨数（数量）不能为空")
    private BigDecimal ton;

    /**
     * 出入库类型 （1.出库   2入库）
     */
    private String inoutType;

    /**
     * 进出场类型code 字典INOUT_STORAGE （理货、作业票、补录、调账、清场、混配）
     */
    @NotBlank(message = "进出场类型编码不能为空")
    private String inoutStorageCode;

    /**
     * 进出场类型name
     */
    @NotBlank(message = "进出场类型名称不能为空")
    private String inoutStorageName;

    /**
     * 作业票表ID
     */
    private Long workTicketId;

    /**
     * 作业票子表ID
     */
    private Long workTicketDetailId;

    /**
     * 理货主表id
     */
    private Long cargoTallyId;

    /**
     * 理货子表id
     */
    private Long cargoTallyDetailId;

    /**
     * 进出场日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "进出场日期不能为空")
    private Date inoutDate;

    /**
     * 作业公司ID
     */
//    @NotNull(message = "作业公司ID不能为空")
    private Long companyId;

    /**
     * 作业公司NAME
     */
//    @NotBlank(message = "作业公司名称不能为空")
    private String companyName;

    /**
     * 是否清场 0否/1是
     */
    private String cleanMassSign;

    /**
     * 清垛人-ID
     */
    private Long cleanMassBy;

    /**
     * 清垛人-姓名
     */
    private String cleanMassByName;

    /**
     * 清垛时间
     */
    private Date cleanMassTime;

    //回显理货人用
    private String tallyMan;
    //回显理货时间用
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date tallyTime;
    //回显运输车号用
    private String transportEquipmentNo;
    /**
     * 货转id
     */
    private Long cargoTransferId;

    /**
     * 票货混配记录ID，对应混配新票货；进出场类型为混配时，票货混配记录ID与票货混配明细ID必含其一
     */
    private Long cargoMixRecordId;

    /**
     * 票货混配明细ID，对应混配原票货；进出场类型为混配时，票货混配记录ID与票货混配明细ID必含其一
     */
    private Long cargoMixDetailId;

    /**
     * 用于倒运，标记垛位是源或者目标，源：1，目标：2
     */
    private String sourceOrTarget;

    private String storageCreateByName;

    private Date storageCreateTime;
}
