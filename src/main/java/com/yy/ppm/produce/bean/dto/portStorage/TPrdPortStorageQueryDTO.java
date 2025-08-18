package com.yy.ppm.produce.bean.dto.portStorage;

import com.alibaba.excel.annotation.ExcelIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-24 15:21
 */
@Setter
@Getter
public class TPrdPortStorageQueryDTO {

    /**
     * 作业公司ID
     */
    private Long companyId;

    /**
     * 航次子表ID
     */
    private Long shipvoyageItemId;

    /**
     * 航次子表ID
     */
    private Long shipVoyageId;

    /**
     * scn
     */
    private String scn;

    /**
     * 查询条件（场、区、垛）
     */
    private String queryStr;

    /**
     * 内外贸
     */
    private String tradeType;

    /**
     * 库场ID
     */
    private Long storehouseId;

    /**
     * 区域ID
     */
    private Long regionId;

    /**
     * 堆ID
     */
    private Long massId;

    /**
     * 货主ID
     */
    private Long cargoOwnerId;

    /**
     * 货物编码
     */
    private String cargoName;

    /**
     * 包装编码
     */
    private String packingCode;

    /**
     * 票货号
     */
    private String cargoInfoNo;

    /**
     * 船名
     */
    private String shipName;

    /**
     * 航次
     */
    private String voyage;

    /**
     * 是否清场,INOUT_STORAGE_CODE
     */
    private String inoutStorageCode;

    /**
     * 是否清场
     */
    private String inoutStorageName;


}
