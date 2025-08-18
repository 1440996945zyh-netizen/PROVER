package com.yy.ppm.dispatch.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName 拖轮服务记录(TDisTugServiceRecord)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:45:00
 */
@Data
public class TDisTugServiceRecordPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -72943221206618253L;

    /** 主键ID */
    private Long id;
    /** 航次ID */
    private Long shipvoyageId;
    private Long shipvoyageItemId;
    /** 船舶ID */
    private Long shipId;
    /** 中文船名 */
    private String shipName;

    /** 船名航次 */
    private String shipVoyage;
    /** scn */
    private String scn;
    /** 动态id */
    private Long shipDynamicId;
    /** 拖轮id */
    @NotNull(message = "拖轮id不能为空")
    private Long tugId;
    /** 拖轮名称 */
    @NotBlank(message = "拖轮名称不能为空")
    private String tugName;
    /** 服务内容code（字典 TUG_SERVICE _TYPE） */
    @NotBlank(message = "服务内容编码不能为空")
    private String tugServiceType;
    /** 服务内容name（协助靠泊、协助离泊、协助移泊、演习、海事使用、特殊情况抢险） */
    @NotBlank(message = "服务内容名称不能为空")
    private String tugServiceTypeName;
    /** 开始时间 */
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date startTime;
    /** 结束时间 */
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date endTime;
    /** 服务时长（小时） */
    private BigDecimal timeLength;
    /** 备注 */
    private String remark;
    /**
     * 是否标准使用  1:是；0：否
     */
    private String isStandardUse;

    /**
     * 非标准原因 DIS_TUG_REASON
     */
    private Long reasonCode;
    /**
     * 非标准原因
     */
    private String reasonName;
}

