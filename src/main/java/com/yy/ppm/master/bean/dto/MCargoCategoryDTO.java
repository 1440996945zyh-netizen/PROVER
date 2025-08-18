package com.yy.ppm.master.bean.dto;

import com.yy.ppm.master.bean.po.MCargoCategoryPO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * (MCargoType)DTO
 *
 * @author makejava
 * @date 2021-03-08 11:17:32
 */
@Getter
@Setter
@ToString
public class MCargoCategoryDTO extends MCargoCategoryPO implements Serializable {

    private static final long serialVersionUID = 558105537731856484L;

    /**货物种类*/
    private String cargoClassNm;
    /**是否在用 （0：停用， 1在用）*/
    private String statusLabel;
    /**是否主要货种 （0：否 1：是）*/
    private String isMainLabel;

}
