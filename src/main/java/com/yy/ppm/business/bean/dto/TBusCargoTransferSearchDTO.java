package com.yy.ppm.business.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @ClassName 货权转移记录表(TBusCargoTransfer)SearchDTO
 * @author yy
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023年07月03日 19:37:00
 */
@Data
public class TBusCargoTransferSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 209445098006446012L;

            /**主键ID*/
    private Long id;
            /**货转日期*/
    private String transferDate;
            /**原票货ID*/
    private Long cargoInfoIdSource;
            /**目标票货ID*/
    private Long cargoInfoIdTarget;
            /**货转件数*/
    private Long quantity;
            /**货转重量*/
    private BigDecimal ton;
            /**目标货主ID*/
    private Long cargoOwnerId;
            /**目标货主名称*/
    private String cargoOwnerName;
            /**目标货代ID*/
    private Long cargoAgentId;
            /**目标货代名称*/
    private String cargoAgentName;
            /**堆存费起算日期*/
    private Date storageDate;
            /**状态：1:待审核，10:已审核*/
    private String status;
            /**备注*/
    private String remark;
            /**创建者-ID*/
    private Long createBy;
            /**创建者-姓名*/
    private String createName;

    /**原货主ID*/
    private Long oldCargoOwnerId;
    /**货物代码*/
    private String cargoCode;
    /**贸别*/
    private String tradeType;
    /**航次id*/
    private Long shipvoyageItemId;
    /**
     * 票货号
     */
    private String cargoInfoNo;
    private String isYard;

    private String shipName;

    private String voyage;
}

