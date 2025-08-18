package com.yy.ppm.master.bean.dto;

import com.yy.ppm.master.bean.po.MCargoPO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * (MCargo)DTO
 *
 * @author makejava
 * @date 2021-03-08 11:17:32
 */
@Getter
@Setter
@ToString
public class MCargoDTO extends MCargoPO implements Serializable {

    private static final long serialVersionUID = -75244144924523860L;

    /**状态0：停用；1：在用*/
    private String statusLabel;

    private String cargoCategoryName;
    private String cargoTypeName;
    private String workTypeName;
    private String outwardType;
    private String outwardPacking;


}
