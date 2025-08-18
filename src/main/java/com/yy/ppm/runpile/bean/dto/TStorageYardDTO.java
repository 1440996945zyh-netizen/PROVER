package com.yy.ppm.runpile.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.yy.ppm.common.bean.po.BasePO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 库场PO
 */
@Getter
@Setter
@ToString
public class TStorageYardDTO extends BasePO implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1955579813519107745L;
	/**
     * 主键
     */
    private Long id;
    /**
     * 父主键
     */
    private Long parentId;
    /**
     * 名称
     */
    private String storageYardNm;
    /**
     * 助记码
     */
    private String shortCd;
    /**
     * 面积
     */
    private BigDecimal area;
    /**
     * 场地类型
     */
    private String storageYardTypeCd;
    /**
     * 是否港内货场 1:是；0：否
     */
    private String isInnerStorageYard;
    /**
     * 单位面积承载吨
     */
    private BigDecimal unitAreaTon;
    /**
     * 是否外租
     */
    private String isRent;
    /**
     * 工作区域
     */
    private String workAreaCd;
    /**
     * 排序号
     */
    private Integer sortNum;
    /**
     * 场地等级，便于查询 1：场；2区；3垛位
     */
    private String storageYardLevel;
}
