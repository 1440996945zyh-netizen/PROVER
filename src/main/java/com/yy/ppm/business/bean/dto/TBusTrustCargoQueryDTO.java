package com.yy.ppm.business.bean.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class TBusTrustCargoQueryDTO extends PageParameter implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 216157791720703858L;
	
	/** 主键ID */
    private Long id;
    
    /** 指令ID */
    private Long trustId;
    
    /** 票货ID */
    private Long cargoInfoId;
    
    /** 方向（1源，2目标） */
    private String direction;
    
    /** 计划件数 */
    private Long quantity;
    
    /** 计划重量 */
    private BigDecimal ton;
    
    /** 货主ID */
    private Long cargoOwnerId;
    
    /** 货主名称 */
    private String cargoOwnerName;
    
    /** 货代ID */
    private Long cargoAgentId;
    
    /** 货代名称 */
    private String cargoAgentName;
    
    /** 货物代码 */
    private String cargoCode;
    
    /** 货物名称 */
    private String cargoName;
    
    /** 包装代码（字典：PACKING） */
    private String packingCode;
    
    /** 包装名称 */
    private String packingName;
    
    /** 渤海通ID */
    private Integer bhtId;
    
    /** 作业伴随ID */
    private Long workAccompanyingId;
    
    /** 合同ID **/
    private Long contractId;
    
    /** 合同费率ID **/
    private Long contractRateId;
    
    /** 费率 **/
    private BigDecimal rate;
    
    /** 预估金额 **/
    private BigDecimal estAmount;
    
    /** 合同编号 **/
    private String contractName;

    /**
     * 业务号（指令编号+两位流水）
     */
    private String businessNo;

    /**
     * 舱口号
     */
    private String hatchs;

    /**
     * 是否二次过磅 0否/1是
     */
    private String isSecondWeigh;

    /**
     * 打印次数是否有效 0否/1是
     */
    private String printPoundId;

    /**
     * 打印次数
     */
    private Integer printPoundNum;
    
    /**
     * 票货号
     */
    private String cargoInfoNo;
    
    private String voyage;
    
    private String shipName;
    
    /**
     * 计划号
     */
    private String trustNo;
    
    /**
     * 指令票货关联表id
     */
    private Long trustCargoId;

    /**
     * 内倒类型
     */
    private String innertransportType;
    /**
     * 状态 1可用 0不可用
     */
    private Long status;
    /**
     * 内倒过磅顺序，1先轻后重2先重后轻
     */
    private Long transportType;

    /**
     * 是否启用内倒规则
     */
    private String isRule;
    /**
     * 空车重量不高于
     */
    private BigDecimal emptyWeight;
    /**
     * 重车重量不低于
     */
    private BigDecimal heavyWeight;
    /**
     * 港区code
     */
    private String workAreaCd;
    /**
     * 港区name
     */
    private String workAreaNm;

    /**
     * 船舶航次ID
     */
    private Long shipvoyageItemId;
    /**
     * 开始日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     * 结束日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private String trustType;
}