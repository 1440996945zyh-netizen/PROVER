package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;

/**
 * 设备台账信息PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MEquipmentInfoPO extends BasePO {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 设备大类ID
     */
    private Long equipBigCategoryId;

    /**
     * 设备大类名称
     */
    private String equipBigCategoryName;
    private String netValue;

    /**
     * 设备中类ID
     */
    private Long equipMiddleCategoryId;

    /**
     * 设备中类名称
     */
    private String equipMiddleCategoryName;

    /**
     * 设备小类ID
     */
    private Long equipSmallCategoryId;

    /**
     * 设备小类名称
     */
    private String equipSmallCategoryName;

    /**
     * 设备名称
     */
    private String equipName;

    /**
     * 规格编号
     */
    private String specificCode;

    /**
     * 设备规格
     */
    private String equipSpecification;

    /**
     * 型号
     */
    private String modelNumber;

    /**
     * 设备编码
     */
    private String equipCode;

    /**
     * 设备系统编码
     */
    private String equipSystemCode;

    /**
     * 设备技术状态（1.一类，2.二类，3.三类，4.四类）
     */
    private String equipTechState;

    /**
     * 设备技术状态名称
     */
    private String equipTechStateName;

    /**
     * 设备状态（01.在用，02.在修，03.出租，04.封存，05.报废，06.停用，07.转卖）
     */
    private String equipState;

    /**
     * 设备状态名称
     */
    private String equipStateName;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 计量单位名称
     */
    private String unitName;

    /**
     * 保险期限
     */
    private String insuranceDate;

    /**
     * 所属单位
     */
    private Long useCompanyId;

    /**
     * 所属部门
     */
    private Long useOrgId;

    /**
     * 是否特种设备（0.否，1.是）
     */
    private String isParticular;

    /**
     * 设备经度
     */
    private BigDecimal longitude;

    /**
     * 设备纬度
     */
    private BigDecimal latitude;

    /**
     * 设备责任人
     */
    private Long responsiCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 资产编号
     */
    private String assetsNo;

    /**
     * 资产名称
     */
    private String assetsName;

    /**
     * 能源类型
     */
    private String sourceType;

    /**
     * 能源类型名称
     */
    private String sourceTypeName;

    /**
     * 审批来源
     */
    private String approveType;

    /**
     * 资产数量
     */
    private Integer assetsNum;

    /**
     * 资产类别
     */
    private Long assetsCategoryType;

    /**
     * 运行状态（枚举）
     */
    private Integer runStatus;

    /**
     * 能耗类型
     */
    private Long energyStatus;

    /**
     * 油号
     */
    private Long oilNumType;

    /**
     * 项目类别
     */
    private Long itemCategory;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 1：设备，2：设施
     */
    private Integer assetsType;

    /**
     * 流程主键
     */
    private Long flowId;

    /**
     * 使用单位（法人单位）
     */
    private String pkUsedorg;

    /**
     * 设备管理组织（法人单位）
     */
    private String pkOwnerorg;

    /**
     * 生产用途
     */
    private String define4;

    /**
     * 考核类型
     */
    private String define8;

    /**
     * 是否固定资产
     */
    private String define13;

    /**
     * 固机流机
     */
    private String define14;

    /**
     * 是否非道路移动机械
     */
    private String define15;

    /**
     * 是否通过环保检测
     */
    private String define18;

    /**
     * 是否为自动化或远控设备
     */
    private String define19;

    /**
     * 是否经过防风能力评估
     */
    private String define20;

    /**
     * 是否经过钢结构检测
     */
    private String define21;

    /**
     * 是否安装自动在线检测系统
     */
    private String define22;

    /**
     * 是否列入老旧设备更新计划（计划时间）
     */
    private String define23;

    /**
     * 制度台时
     */
    private String define109;

    /**
     * ABC分类
     */
    private String define28;

    /**
     * 车辆年审
     */
    private String define44;

    /**
     * 设备组织
     */
    private String pkOrg;

    /**
     * 删除标志（0-未删除，1-已删除）
     */
    private Integer delFlag;

    /**
     * 删除人
     */
    private String deleteBy;

    /**
     * 删除人姓名
     */
    private String deleteByName;

    /**
     * 删除时间
     */
    private java.util.Date deleteTime;

    /**
     * 出厂编号
     */
    private String factoryNumber;

    /**
     * 设备自重(T)
     */
    private BigDecimal equipWeight;

    /**
     * 设备购置时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private java.util.Date purchaseTime;

    /**
     * 设备使用时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private java.util.Date usageTime;

    /**
     * 供货单位
     */
    private String supplierUnit;

    /**
     * 制造厂家
     */
    private String manufacturer;

    /**
     * 排放标准
     */
    private String emissionStandard;

    /**
     * 发动机功率/装机容量(Kw)
     */
    private BigDecimal enginePower;

    /**
     * 设备原值(元)
     */
    private BigDecimal originalValue;

    /**
     * 折旧期限(月)
     */
    private Integer depreciationPeriod;

    /**
     * 已折旧期限(月)
     */
    private Integer depreciatedPeriod;
}

