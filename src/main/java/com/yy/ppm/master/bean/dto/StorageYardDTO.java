package com.yy.ppm.master.bean.dto;

import com.yy.ppm.master.bean.po.StorageYardPO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 库场DTO
 */
@Getter
@Setter
@ToString
public class StorageYardDTO extends StorageYardPO {

    /**
     * 场地类型
     */
    private String storageYardTypeNm;
    /**
     * 是否港内货场 1:是；0：否
     */
    private String isInnerStorageYardLabel;
    /**
     * 是否外租
     */
    private String isRentLabel;
    /**
     * 工作区域
     */
    private String workAreaNm;
    /**
     * 是否有子级
     */
    private Boolean hasChildren;
    /**
     * 是否是叶子节点
     */
    private Boolean isLeaf;
    private String isBondedAreaLabel;

    private String positionName;
}
