package com.yy.ppm.statement.bean.dto.busHandoverlist;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-10-09 9:37
 */
@Setter
@Getter
public class TBusCargoInfoDTO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 航次ID
     */
    private Long shipvoyageId;
    /**
     * 航次子表ID
     */
    private Long shipvoyageItemId;
    /**
     * SCN
     */
    private String scn;
    /**
     * 船名
     */
    private String shipName;
    /**
     * 票货号（自动生成，如果是货权转移生成的在原票货号上追加序号）
     */
    private String cargoInfoNo;
    /**
     * 货主ID
     */
    private Long cargoOwnerId;
    /**
     * 货主名称
     */
    private String cargoOwnerName;
    /**
     * 货代ID
     */
    private Long cargoAgentId;
    /**
     * 货代名称
     */
    private String cargoAgentName;
    /**
     * 货物代码
     */
    private String cargoCode;
    /**
     * 货物名称
     */
    private String cargoName;
    /**
     * 贸别，内贸、外贸
     */
    private String tradeType;
    /**
     * 包装代码（字典：PACKING）
     */
    private String packingCode;
    /**
     * 包装名称
     */
    private String packingName;
    /**
     * 作业公司ID
     */
    private Long companyId;
    /**
     * 作业公司NAME
     */
    private String companyName;
    /**
     * 父ID
     */
    private Long parentId;
    /**
     * 根票货ID
     */
    private Long rootId;
    /**
     * 件数
     */
    private Integer quantity;
    /**
     * 重量
     */
    private BigDecimal ton;
    /**
     * 货权量
     */
    private BigDecimal rightsQuantity;
    /**
     * 剩余货权量
     */
    private BigDecimal surplusRightsQuantity;
    /**
     * 是否完货 0：否 1：是
     */
    private String isClear;
    /**
     * 清场人-ID
     */
    private Long clearBy;
    /**
     * 清场人-姓名
     */
    private String clearByName;
    /**
     * 清场日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date clearDate;

    /**
     * 指令票货ID
     */
    private Long trustCargoId;
    
    /**
     * 票货
     */
    private Integer ticketNum;

    private BigDecimal weightGoods;
    //集港过磅量
    private BigDecimal JGweightGoods;
    //疏港过磅量
    private BigDecimal SGweightGoods;
    /**
     * 提运单号
     */
    private String deliveryNumbers;
}
