package com.yy.ppm.tallyExtrinsic.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.tallyExtrinsic.bean.po.TYardTallyItemPO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * 港存(AppTallyLadingDTO)PO
 *
 * @author chenfs
 * @date 2023-09-18 15:53:36
 */

@Getter
@Setter
@ToString
public class AppTallyLadingDTO extends BasePO {
    /**出入库ID*/
    @JsonSerialize(using = ToStringSerializer.class)
    private Long inOutId;
    /**清单ID*/
    @JsonSerialize(using = ToStringSerializer.class)
    private Long loadingListId;
    /**件号（钢材理货时需要）*/
    private String pieceNo;
    /**炉号（钢材理货时需要）*/
    private String heatNo;
    /**材质（钢材理货时需要）*/
    private String material;
    /**规格（钢材理货时需要）*/
    private String specs;
    /**件数*/
    private Integer quantity;
    /*** 剩余件数*/
    private Integer quantitySurplus;
    /*** 出库件数*/
    private Integer quantityOut;
    /**重量*/
    private Double ton;
    /*** 剩余重量*/
    private BigDecimal tonSurplus;
    /*** 出库重量*/
    private Double tonOut;
    /**体积*/
    private BigDecimal volume;
    /**货主ID*/
    private String cargoOwnerId;
    /**货主名称*/
    private String cargoOwnerName;
    /**货代ID*/
    private String cargoAgentId;
    /**货代名称*/
    private String cargoAgentName;
    /**货物代码*/
    private String cargoCode;
    /**货物名称*/
    private String cargoName;
    /**库场ID*/
    private Long storehouseId;
    private Long storehouseTargetId;
    /**
     * 票货ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cargoInfoId;
    /**库场名称*/
    private String storehouseName;
    private String storehouseTargetName;
    /**货位ID*/
    private Long locationId;
    private Long locationTargetId;
    /**货位编号*/
    private String locationNo;
    private String locationTargetNo;
    /**垛位号*/
    @JsonSerialize(using = ToStringSerializer.class)
    private String stackPositionId;
    private String stackPositionTargetId;
    private String stackPositionName;
    private String stackPositionTargetName;
    /**车号*/
    private String  truckNo;
    private String  cargoInfoNo;

    private List<TYardTallyItemPO> listTallyItemList;

}

