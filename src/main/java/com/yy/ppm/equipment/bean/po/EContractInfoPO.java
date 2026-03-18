package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 设备调拨PO
 * @author system
 */
@Getter
@Setter
@ToString
public class EContractInfoPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;
    // 主键ID
    private Long id;

    // 合同名称
    private String contractName;

    // 合同编号
    private String contractCode;

    // 合同金额
    private BigDecimal contractAmount;

    // 合同开始日期
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    // 合同截止日期
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    // 适用范围(设备ID)，多个以逗号分隔
    private String applyScope;

    // 合同状态，默认1(有效)
    private String status;
    private String contractType;

    // 创建人
    private Long createdUser;

    // 创建时间
    private Date createdTime;

    // 修改人
    private Long updateUser;

    // 修改时间
    private Date updateTime;

    /**
     * 维修单位id
     */
    private String externalCompanyId;

}
