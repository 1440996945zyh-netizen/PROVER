package com.yy.ppm.master.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * (MCargoType)SearchDTO
 *
 * @author makejava
 * @date 2021-03-08 11:17:32
 */
@Getter
@Setter
@ToString
public class MCargoCategorySearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -65573951347068755L;
    /**货种编号*/
    @NotEmpty
    private String cargoCategoryCode;


    /** 火种名称*/
    @NotEmpty
    private String cargoCategoryName;

    /**货种编号*/
    @NotEmpty
    private String cargoCode;


    /**  */
    @NotEmpty
    private String cargoName;


    /** 货类编号 (字典)*/
    @NotEmpty
    private String cargoTypeCode;

    /** 删除标志*/
    @NotEmpty
    private char delFlag;


}
