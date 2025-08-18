package com.yy.ppm.business.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 票货信息表(TBusCargoInfo)PO
 * @Description
 * @createTime 2023年07月03日 18:47:00
 */
@Data
public class TBusCargoInfoPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 424959525558418701L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 航次ID
     */
    private Long shipvoyageId;
    /**
     * 航次子表ID
     */
    private Long shipvoyageItemId;
    /**
     * SCN
     */
    private String scn;
    /**
     * 船名
     */
    private String shipName;
    /**
     * 票货号（自动生成，如果是货权转移生成的在原票货号上追加序号）
     */
    private String cargoInfoNo;
    private String customerCode;
    /**
     * 货主ID
     */
    private Long cargoOwnerId;
    /**
     * 货主名称
     */
    private String cargoOwnerName;
    /**
     * 货代ID
     */
    private Long cargoAgentId;
    /**
     * 货代名称
     */
    private String cargoAgentName;
    /**
     * 货物代码
     */
    private String cargoCode;
    /**
     * 货物名称
     */
    private String cargoName;
    /**
     * 贸别，内贸、外贸
     */
    private String tradeType;
    /**
     * 合同贸别，内贸、外贸
     */
    private String inteFore;
    /**
     * 包装代码（字典：PACKING）
     */
    private String packingCode;
    /**
     * 包装名称
     */
    private String packingName;
    /**
     * 作业公司ID
     */
    private Long companyId;
    /**
     * 作业公司NAME
     */
    private String companyName;
    /**
     * 父ID
     */
    private Long parentId;
    /**
     * 根票货ID
     */
    private Long rootId;
    /**
     * 件数
     */
    private Long quantity;
    /**
     * 重量
     */
    private BigDecimal ton;
    /**
     * 货权量
     */
    private BigDecimal rightsQuantity;
    /**
     * 剩余货权量
     */
    private BigDecimal surplusRightsQuantity;
    /**
     * 是否完货 0：否 1：是
     */
    private String isClear;
    /**
     * 清场人-ID
     */
    private Long clearBy;
    /**
     * 清场人-姓名
     */
    private String clearByName;
    /**
     * 清场日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date clearDate;
    /**
     * 堆存费起算日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date storageDate;

    /**
     * 堆存费结算状态（字典：HANDOVERLIST_STATUS）
     */
    private String statementStatusCode;

    /**
     * 堆存费结算状态（字典：HANDOVERLIST_STATUS 10未结算、20已预结、30最终结算）
     */
    private String statementStatusName;

    /**
     * 票货来源：10：指令 20：票货新增 30：货权转移 40：拆灌包 50：混配
     */
    private String source;

    /**
     * 生成该票货的指令ID
     */
    private Long trustId;

    /**
     * 老系统合同子表ID
     */
    private String contractItemId;

    /**
     * 老系统合同代码
     */
    private String contractCode;

    /**
     * 航次
     */
    private String voyage;
    /**
     * 剩余免堆存期
     */
    private Integer residueStorage;
    /**
     * 货转日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date transferDate;

    /**
     * 原始票货号（初始化中港区数据使用）
     */
    private String ysphh;

    /**
     * 结算状态
     */
    private String jszt;

    /**
     * TOS系统票货号ID
     */
    private String tosCargoId;

    /**
     * 货转关系
     */
    private String cdh;

    private String isTos;

    /**
     * 混配剩余免堆期
     */
    private Integer mixFreeStorageDays;

    /**
     * 完货日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date realClearDate;

    //是否作废 10已作废  20未作废
    private String isLogout;

    /**
     * 作废人
     */
    private Long logoutBy;

    /**
     * 作废人姓名
     */
    private String logoutByName;

    /**
     * 作废时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date logoutTime;


    /**
     * 舱口 HANGER_NUMS
     */
    private String hatchNums;

    /**
     * 提运单id
     */
    private Long deliveryId;

    /**
     * 提运单号
     */
    private String deliveryNumbers;
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date calEndDate;
}

