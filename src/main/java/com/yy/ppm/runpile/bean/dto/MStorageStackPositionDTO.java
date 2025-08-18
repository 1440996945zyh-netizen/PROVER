package com.yy.ppm.runpile.bean.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.yy.common.page.PageParameter;
import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.runpile.bean.po.MStorageStackPositionPO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MStorageStackPositionDTO extends BasePO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6742065244343795320L;

    private Long id;
    private Long stackId;
    private String stackCode;
    private String stackName;
    private String position;
    private String positionFrom;
    private Date positionTime;
    private String storageYardIndex;
    private String storageYardLon;
    private String storageYardLat;
    private String delFlag;
    private String freehandSketching;
    private List<MStorageStackPositionPO> storageStackPositionList = new LinkedList<>();
}
