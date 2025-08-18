package com.yy.ppm.business.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 票货混配记录表(TBusCargoMixRecord)PO
 *
 * @author linqi
 * @since 2024-03-04 16:31:45
 */
@Setter
@Getter
public class TBusCargoMixRecordPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 货主ID
     */
    @NotNull(message = "货主ID不能为空")
    private Long cargoOwnerId;

    /**
     * 货物编码
     */
    @NotBlank(message = "货物编码不能为空")
    private String cargoCode;

    /**
     * 库场ID
     */
    @NotNull(message = "库场ID不能为空")
    private Long storehouseId;

    /**
     * 区域ID
     */
    @NotNull(message = "区域ID不能为空")
    private Long regionId;

    /**
     * 垛位ID
     */
    @NotNull(message = "垛位ID不能为空")
    private Long massId;

    /**
     * 新票货ID
     */
    private Long cargoInfoId;

    /**
     * 混配重量
     */
    private BigDecimal mixWeight;

    /**
     * 混配时间
     */
    @NotNull(message = "混配时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date mixTime;

    /**
     * 审核状态 10未审核/20已审核
     */
    private String status;

    //船名航次信息
    private Long shipvoyageItemId;
    private Long shipvoyageId;
    private String shipName;
    private String voyage;

    /**
     * 是否生成混配杂项费
     */
    private String isBilling;
}

