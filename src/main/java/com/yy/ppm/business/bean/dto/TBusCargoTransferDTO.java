package com.yy.ppm.business.bean.dto;


import com.yy.ppm.business.bean.po.TBusCargoTransferPO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName 货权转移记录表(TBusCargoTransfer)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月03日 19:37:00
 */
@Data
public class TBusCargoTransferDTO extends TBusCargoTransferPO {

    private static final long serialVersionUID = 575231257950234635L;

    /** 附件信息 */
    private List<Long> fileIds;

    private String shipName;


    private String cargoCode;
    private String cargoName;
    private String tradeType;

    private String packingName;

    /** 目标货主ID */
    private Long cargoOwnerIdTarget;
    /** 目标货主名称 */
    private String cargoOwnerNameTarget;
    /** 目标货代ID */
    private Long cargoAgentIdTarget;
    /** 目标货代名称 */
    private String cargoAgentNameTarget;

    /** 原货主ID */
    private Long cargoOwnerIdSource;
    /** 原货主名称 */
    private String cargoOwnerNameSource;
    /** 原货代ID */
    private Long cargoAgentIdSource;
    /** 原货代名称 */
    private String cargoAgentNameSource;

    /**
     * 源票货号
     */
    private String sourceCargoInfoNo;
    /**
     * 目标票货号
     */
    private String targetCargoInfoNo;
    /**
     * 垛位
     */
    private String massName;

    private String packingCode;

    private Long companyId;

    private String companyName;

    private Long shipvoyageItemId;

    private String shipNameVoyage;


}
