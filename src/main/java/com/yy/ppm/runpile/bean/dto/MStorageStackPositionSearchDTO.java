package com.yy.ppm.runpile.bean.dto;

import java.io.Serializable;
import java.util.Date;

import com.yy.common.page.PageParameter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MStorageStackPositionSearchDTO extends PageParameter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6742065244343795320L;

    private Long id;
    private Long stackId;
    private Long massId;
    private String stackCode;
    private String stackName;
    private String position;
    private String positionFrom;
    private Date positionTime;
    private String storageYardIndex;
    private String storageYardLon;
    private String storageYardLat;
    private String delFlag;
    private String startWorkDate;
    private String endWorkDate;
}
