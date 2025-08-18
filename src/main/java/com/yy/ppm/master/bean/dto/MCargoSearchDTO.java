package com.yy.ppm.master.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * (MCargo)SearchDTO
 *
 * @author makejava
 * @date 2021-03-08 11:17:32
 */
@Getter
@Setter
@ToString
public class MCargoSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -38375756635077675L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 渤海通id
     */
    private String bhtId;

    /**
     * 货物编号 自动采番，根据货种算12位
     */
    private String cargoCode;

    /**
     * 货名
     */
    private String cargoName;

    /**
     * 简称
     */
    private String cargoShortName;

    /**
     * 所属货种代码
     */
    private String cargoCategoryCode;

    /**
     * 作业模式，1件杂、2散杂、3木材
     */
    private Integer workType;

    /**
     * 件杂货理货方式1件号0件数
     */
    private Integer tally;

    /**
     * 货物颜色
     */
    private String cargoColor;

    /**
     * 排序
     */
    private Integer sortNum;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private char delFlag;


    /***
     * 主列表查询用
     */
    private String cargoTypeCode;

    /**
     * 货种名称 主列表查询用
     */
    private String cargoCategoryName;

    /**
     * 状态 0停用/1启用
     */
    private String status;

}
