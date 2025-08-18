package com.yy.ppm.business.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @ClassName 票货信息表(TBusCargoInfo)SearchDTO
 * @author yy
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023年07月03日 18:47:00
 */
@Data
public class TBusCargoInfoSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 131733874565661120L;

            /**主键ID*/
    private Long id;
            /**航次ID*/
    private Long shipvoyageId;
            /**航次子表ID*/
    private Long shipvoyageItemId;
            /**SCN*/
    private String scn;
            /**船名*/
    private String shipName;
            /**票货号（自动生成，如果是货权转移生成的在原票货号上追加序号）*/
    private String cargoInfoNo;
            /**货主ID*/
    private Long cargoOwnerId;
            /**货主名称*/
    private String cargoOwnerName;
            /**货代ID*/
    private Long cargoAgentId;
            /**货代名称*/
    private String cargoAgentName;
            /**货物代码*/
    private String cargoCode;
            /**货物名称*/
    private String cargoName;
            /**贸别，内贸、外贸*/
    private String tradeType;
            /**包装代码（字典：PACKING）*/
    private String packingCode;
            /**包装名称*/
    private String packingName;
            /**作业公司ID*/
    private Long companyId;
            /**作业公司NAME*/
    private String companyName;
            /**父ID*/
    private Long parentId;
            /**根票货ID*/
    private Long rootId;
            /**件数*/
    private Long quantity;
            /**重量*/
    private BigDecimal ton;
            /**货权量*/
    private BigDecimal rightsQuantity;
            /**剩余货权量*/
    private BigDecimal surplusRightsQuantity;
            /**是否完货 0：否 1：是*/
    private String isClear;
            /**清场人-ID*/
    private Long clearBy;
            /**清场人-姓名*/
    private String clearByName;
            /**清场日期*/
    private Date clearDate;
            /**创建者-ID*/
    private Long createBy;
            /**创建者-姓名*/
    private String createByName;
                            /**更新者-姓名*/
    private String updateByName;
    /**合同号*/
    private String contractCode;
    /**航次*/
    private Long voyage;
    private String workType;
    private String businessNo;
    private String isLogout;
    private String startDate;
    private String endDate;
            }

