package com.yy.ppm.customs.bean;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TosPlanDTO {
	
	private String autoId;// id:唯一标识	byte		
	private String scn;// 港口系统航次	byte		
	private String bizType;// 内、外贸（my01内贸 my02外贸ka08空车）	byte		
	private String icCard;// ic卡号、rfid卡号	byte		
	private String trafName;// 船名	byte		
	private String trafCode;// 船代码或编号	byte		
	private String voyageNo;// 标准航次	byte		
	private String tradeName;// 货主	byte		
	private String goodsName;// 货物名称	byte		
	private String billNo;// 提运单号	byte		
	private String entryId;// 报关单号	byte		
	private String carNo;// 车牌号	byte		
	private BigDecimal packNo;// 件数	byte		
	private String goodsType;// 货物类型	byte		
	private BigDecimal goodsV;// 货物体积	byte		
	private BigDecimal goodsWt;// 净重	byte		
	private BigDecimal grossWt;// 毛重	byte		
	private BigDecimal tareWt;// 皮重	byte		
	private String isinvalid;// 是否作废（1：作废、0：正常）	byte		
	private String areaCode;// 'jn30'	区域代码（jn30）	byte		
	private String customsCode;// '4310'	海关关区代码（4310）	byte		
	private String extendField1;// 
	private String extendField2;// 
	private String extendField3;// 	
	private Date extendField4;// 	
	private BigDecimal extendField5;// 
	private String inputCode;// 'sdgk666666'	录入单位代码（sdgk666666）	byte		
	private String inputName;// '山东省港口集团潍坊港有限公司'	录入单位名称（山东省港口集团潍坊港有限公司）	byte		
	private String declareCode;// 'sdgk666666'	申报单位代码（sdgk666666）	byte		
	private String declareName;// '山东省港口集团潍坊港有限公司'	申报单位名称（山东省港口集团潍坊港有限公司）	byte		
	private String declarePerson;// '山东省港口集团潍坊港有限公司'	申报人（山东省港口集团潍坊港有限公司）	byte		
	private Date declareDate;// 申报时间	byte		
	private String informNo;// 	
}
