package com.yy.ppm.runpile.bean.dto;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-24 15:21
 */
@Setter
@Getter
public class TRunPilePortStorageDetailDTO{
	
	/**
	 * 跑垛状态 10：不需要跑垛 30：未跑垛20：垛位变化大需要重新跑垛
	 */
	private String runPileState;
	
	/**
	 * 航次子ID
	 */
	private Long shipvoyageItemId;
	private Boolean localStorageChange;  //本场地调账
	private Long fromStorehouseId; 
	private String fromStorehouseName; 
	private Long fromRegionId; 
	private String fromRegionName; 
	private Long fromMassId; 
	private String fromMassName; 
	private Long fromTrustCargoId; //票货id
	private String fromTrustCargoInfo; // 票货信息
	private Long toStorehouseId; 
	private String toStorehouseName; 
	private Long toRegionId; 
	private String toRegionName; 
	private Long toMassId; 
	private String toMassName; 
    private Integer quantity; // 件数
	private BigDecimal ton;  //重量
	private String remark; //备注
	private BigDecimal changeTon;  //变化重量
	
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 港存主表ID
     */
    private Long portStorageId;

    /**
     * 票货ID
     */
    private Long cargoInfoId;

    /**
     * 子作业过程代码
     */
    private String processDetailCode;

    /**
     * 子作业过程名称
     */
    private String processDetailName;

    /**
     * 库场ID
     */
    private Long storehouseId;

    /**
     * 库场名称
     */
    private String storehouseName;

    /**
     * 区域ID
     */
    private Long regionId;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 垛位ID
     */
    private Long massId;
    private Long stackId;// 此字段存在跑垛表中

    /**
     * 垛位名称
     */
    private String massName;

    /**
     * 出入库类型 （1.出库   2入库）
     */
    private String inoutType;

    /**
     * 进出场类型code 字典INOUT_STORAGE （理货、作业票、补录、调账、清场）
     */
    private String inoutStorageCode;

    /**
     * 进出场类型name
     */
    private String inoutStorageName;

    /**
     * 作业票表ID
     */
    private Long workTicketId;

    /**
     * 作业票子表ID
     */
    private Long workTicketDetailId;

    /**
     * 理货主表id
     */
    private Long cargoTallyId;

    /**
     * 理货子表id
     */
    private Long cargoTallyDetailId;

    /**
     * 进出场日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date inoutDate;

    /**
     * 作业公司ID
     */
    private Long companyId;

    /**
     * 作业公司NAME
     */
    private String companyName;

    /**
     * 是否清场 0否/1是
     */
    private String cleanMassSign;

    /**
     * 清垛人-ID
     */
    private Long cleanMassBy;

    /**
     * 清垛人-姓名
     */
    private String cleanMassByName;

    /**
     * 清垛时间
     */
    private Date cleanMassTime;
    
    /**
     * 上次跑垛时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date positionTime;

    /**
     * 港存更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date lastRefreshTime;
    
}
