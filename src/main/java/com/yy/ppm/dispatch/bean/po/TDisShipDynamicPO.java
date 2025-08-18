package com.yy.ppm.dispatch.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 船舶动态表(TDisShipDynamic)PO
 *
 * @author linqi
 * @since 2023-07-12 14:01:06
 */
@Setter
@Getter
public class TDisShipDynamicPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 航次ID
     */
    @NotNull(message = "航次id不能为空")
    private Long shipvoyageId;

    /**
     * 航次子表ID
     */
    private Long shipvoyageItemId;

    /**
     * 船名
     */
    private String shipName;


    /**
     * 航次(抵锚、靠泊、离泊、离港、移泊不填)
     */
    private String voyage;

    /**
     * 装卸,装、卸(抵锚、靠泊、离泊、离港、移泊不填)
     */
    private String loadUnload;

    /**
     * 状态编码(字典SHIPSTATUS)
     */
    @NotBlank(message = "状态编码不能为空")
    private String dynamicTypeCode;

    /**
     * 状态名称(字典 抵锚、靠泊、开工、停工、复工、下舱记录、移泊、完工、离泊、离港、)
     */
    @NotBlank(message = "状态名称不能为空")
    private String dynamicTypeName;

    /**
     * 动态开始时间
     */
    @NotNull(message = "动态开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date dynamicStartTime;

    /**
     * 动态结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date dynamicEndTime;

    /**
     * 泊位ID
     */
    private Long berthId;

    /**
     * 泊位名称
     */
    private String berthName;

    /**
     * 首榄编号
     */
    private String bollardNoStart;

    /**
     * 尾榄编号
     */
    private String bollardNoEnd;

    /**
     * 舷靠
     */
    private String berthType;

    /**
     * 停时类型代码(字典，STOP_TYPE)
     */
    private String stopTypeCode;

    /**
     * 停时类型名称(字典，STOP_TYPE)
     */
    private String stopTypeName;

    /**
     * 停工原因ID
     */
    private Long stopId;

    /**
     * 停工原因名称
     */
    private String stopName;
/**
     * 停时类型Code
     */
    private String stopHourTypeCode;
/**
     * 停时类型NAME
     */
    private String stopHourTypeName;

    /**
     * 备注
     */
    private String remark;
    /**
     * 数据上报同步状态
     */
    private String sjsbStatus;

    /**
     * 停工时长
     */
    private String stopTimeLen;

    private List<TDisTugServiceRecordPO> tugs;
}
