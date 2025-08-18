package com.yy.ppm.appWork.bean.dto;


import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 理货量
 */
@Getter
@Setter
@ToString
public class DunBaoTallyDTO extends BasePO {

    private static final long serialVersionUID = -7734686200034099011L;

    private Long id;
    private String shipVoyageItemId;
    private String trustCargoInfoId;
    private String workType;
    private String workDate;
    private String classCode;
    private String className;
    private String equipmentId;
    private String equipmentNo;
    private String transportEquipmentId;
    private String transportEquipmentNo;
    private String quantity;
    private String ton;
    private String cargoInfoId;
    private String weighbridgeId;
    private String deptParentId;
    private String deptParentName;
    private String operatorsName;
    private String reservationPoundId;//港内短倒唯一标识
    private Long scTallyId;//生产系统理货id



    @Setter
    @Getter
    public static class TallyItem implements Serializable {
        private static final long serialVersionUID = 1L;

        private String cargoInfoId;
        private String stackPositionId;
        private String stackPositionName;
        private String quantity;
        private String ton;
        private String cabinNo;
    }
    private List<TallyItem> listTallyItemList;


}

