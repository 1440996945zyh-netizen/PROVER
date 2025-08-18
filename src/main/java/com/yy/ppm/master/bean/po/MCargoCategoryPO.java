package com.yy.ppm.master.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * (MCargoType)PO
 *
 * @author makejava
 * @date 2021-03-08 11:17:32
 */
@Getter
@Setter
@ToString
public class MCargoCategoryPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -90347000994014876L;

    /**主键*/
    private Long id;


    /**货类编号 （字典)*/
    @NotEmpty
    private String cargoCategoryCode;


    /**货类名称*/
    @NotEmpty
    private String cargoCategoryName;


    /** 货种编号*/
    @NotEmpty
    private String cargoTypeCode;


    /**货种名称*/
    @NotEmpty
    private String cargoTypeName;

    /**主要负责人*/
    private String mainName;
    /**主要负责人电话*/
    private String mainMobile;
    /**次要负责人*/
    private String minorName;
    /**次要负责人电话*/
    private String minorMobile;


    /** 删除标志*/
    @NotEmpty
    private char delFlag;

//    /** 创建者ID*/
//    @NotEmpty
//    private String createBy;
//
//    /**创建者 姓名*/
//    @NotEmpty
//    private String createName;
//
//    /**创建时间*/
//    @NotEmpty
//    private Date createTime;


    /**排序号*/
    @NotEmpty
    private Integer sortNum;


//    /** 更新者ID*/
//    private String updateBy;
//
//
//    /** 更新者 姓名*/
//    private String updateName;
//
//
//    /** 更新时间*/
//
//    private Date updateTime;


}
