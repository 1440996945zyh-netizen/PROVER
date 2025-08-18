package com.yy.ppm.business.bean.dto;


import com.yy.ppm.business.bean.po.TBusTrustPO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @ClassName 作业指令表(TBusTrust)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月05日 09:21:00
 */
@Data
public class TBusTrustDTO extends TBusTrustPO {

    private static final long serialVersionUID = 226414611833554835L;

    private String statusLabel;

    /** 指令货物 */
    List<TBusTrustCargoDTO> cargoList;

    //船舶信息
    Map<String,Object> shipInfo;

    private List<Long> fileIds;

    /**
     * 船名航次
     */
    private String shipNameVoyage;

    /**
     * 场地信息
     */
    private String massNamesTarget;

    private String scn;
    private String consignerId;

    //转水前船名
    private String preChangeShipName;

    //转水前编号
    private String preChangeShipNo;
    private BigDecimal   rate;

    private String deliveryNumbers;

    //标志位 过磅量大于计划量 若大于设置为1变红 小于设置为2不变
    private String tonFlag;
    private Long trustId;



    //委托单主表id
    private Long entrustId;
    //委托单子表id
    private Long entrustDetailId;
    //委托单编号
    private String entrustNo;

    private String disRate;
}
