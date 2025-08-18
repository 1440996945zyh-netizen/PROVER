package com.yy.ppm.business.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName 合同(TBusContract)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 10:48:00
 */
@Data
public class TBusContractPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 101722941111073309L;

    /** 主键ID */
    private Long id;
    /** 合同编号 */
    private String contactNo;
    /** 父合同id */
    private Long parentId;
    /** 作业公司名称 */
    private String companyNames;
    /** 客户ID */
    private Long customerId;
    /** 客户名称 */
    private String customerName;
    /** 签订日期 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date signTime;
    /** 合同类型，1单笔、2年度、3.补充协议 */
    private String contractType;
    /** 是否船舶作业,Y是N否 */
    private String isShip;
    /** 结算依据代码(字典:SETTLEMENT_ BASIS) */
    private String settlementBasisCode;
    /** 结算依据名称（1.货物交接清单数、2.疏港过磅数、3.海关报关单数、4.集港过磅数5.水尺数） */
    private String settlementBasisName;
    /** 结算依据代码(字典:SETTLEMENT_ BASIS) */
    private String expSettlementBasisCode;
    /** 结算依据名称（1.货物交接清单数、2.疏港过磅数、3.海关报关单数、4.集港过磅数5.水尺数） */
    private String expSettlementBasisName;
    /** 外贸结算依据代码(字典:SETTLEMENT_ BASIS) */
    private String outerSettlementBasisCode;
    /** 外贸结算依据名称（1.货物交接清单数、2.疏港过磅数、3.海关报关单数、4.集港过磅数5.水尺数） */
    private String outerSettlementBasisName;
    /** 预缴依据代码(字典:DEPOSIT_ BASIS) */
    private String depositBasisCode;
    /** 预缴依据名称（1.货物交接清单数、2.海关报关单数、3.集港委托书） */
    private String depositBasisName;
    /** 预缴比例 */
    private BigDecimal depositRatio;
    /** 付费方式：10：预付费客户 20：后付费客户  */
    private String payType;
    /** 免堆存天数 */
    private Long freeStorageDays;
    /** 有效期起 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    /** 有效期止 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    /** 航次ID */
    private Long shipvoyageId;
    /** 生效操作人-ID */
    private Long validBy;
    /** 生效操作人-姓名 */
    private String validByName;
    /** 生效操作时间 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date validTime;
    /** 状态，10、签订、20、生效 */
    private String status;
    /** 备注 */
    private String remark;

    private String luxiaoSettlementBasisCode;

    private String luxiaoSettlementBasisName;
}

