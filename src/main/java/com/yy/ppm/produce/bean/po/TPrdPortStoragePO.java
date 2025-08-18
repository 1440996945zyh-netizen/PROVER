package com.yy.ppm.produce.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 港存(TPrdPortStorage)PO
 *
 * @author linqi
 * @since 2023-08-18 15:21:44
 */
@Setter
@Getter
public class TPrdPortStoragePO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 作业公司ID
     */
    private Long companyId;

    /**
     * 作业公司NAME
     */
    private String companyName;

    /**
     * 票货ID
     */
    private Long cargoInfoId;

    /**
     * 库场ID
     */
    private Long storehouseId;

    /**
     * 库场名称
     */
    private String storehouseName;

    /**
     * 区域ID
     */
    private Long regionId;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 垛位ID
     */
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
    private BigDecimal ton;

    /**
     * 1.是   0.否
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
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date cleanMassTime;

    /**
     * 老系统子合同ID
     */
    private String contractItemId;

    private BigDecimal tons;

    /**
     * 初始化港存临时使用
     */
    private String cargoInfoNo;
}

