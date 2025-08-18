package com.yy.ppm.machine.bean.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * 磅单信息表
 * @author zcc
 *
 */
@Getter
@Setter
public class WeightRecordPoundDTO {

	private Long noteId;// 主键
	private String unionNo;// 磅单号	byte		
	private Date weighInDt;// 进港日期时间	byte			
	private String planNo;// 计划单号	byte		
	private String goodsDes;// 目的地	byte		
	private String truckPlate;// 车牌号	byte		 	
	private String tsptId;// 		
	private String workErweiId;// 		
	private String portCode;// 01东作业区02中作业区03西作业区	byte		
	private String scn;// 到港编号	byte		
	private String goodsCode;// 货物代码	byte		
	private String tradeTyp;// 贸别	byte		
}
