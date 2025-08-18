package com.yy.ppm.runpile.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@ToString
public class MStorageStackPositionPO extends BasePO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8015069008464215274L;

	private Long id;
	@NotNull(message = "垛位ID不能为空")
	private Long stackId;
	private String stackCode;
	@NotBlank(message = "垛位名称不能为空")
	private String stackName;
	@NotBlank(message = "点位不能为空")
	private String position;
	private String positionFrom;
	private Date positionTime;
    private String storageYardLon;
    private String storageYardLat;
    private String delFlag;
	private String sideLength;
	private BigDecimal area;
	/**
	 * 是否手绘，1：手绘，2：跑垛
	 */
	private String freehandSketching;
}

