package com.yy.ppm.machine.bean.dto;

import java.io.Serializable;
import java.util.Date;

import com.yy.ppm.common.bean.po.BasePO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 垛位点位DTO
 * @author zcc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TMacTerminalStackPositionDTO extends BasePO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4292983992705760507L;
	
	/**
	 * 垛位id
	 */
    private Long stackId;
    
    /**
     * 垛位code
     */
    private String stackCode;
    
    /**
     * 垛位名称
     */
    private String stackName;
    
    /**
     * 点位,样例[[120.210757727,36.026848046],[120.21075776,36.026860663]]			
     */
    private String position;
    
    /**
     * 点位来源
     */
    private String positionFrom;
    
    /**
     * 点位更新时间
     */
    private Date positionTime;
    
    private Long regionId;
    private String regionName;
	private Long storehouseId;
	private String storehouseName;
}