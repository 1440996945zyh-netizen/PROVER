package com.yy.ppm.appWork.bean.dto;


import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 理货量
 */
@Getter
@Setter
@ToString
public class DunBaoTallyItemDTO extends BasePO {

    private static final long serialVersionUID = -7734686200034099011L;

    private Long id;
    private Long tallyId;
    private String cargoInfoId;
    private String stackPositionId;
    private String stackPositionName;
    private String quantity;
    private String ton;
    private String cabinNo;

}

