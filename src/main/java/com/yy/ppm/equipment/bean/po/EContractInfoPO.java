package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
    private Date startDate;

    // 合同截止日期
    private Date endDate;

    // 适用范围(设备ID)，多个以逗号分隔
    private String applyScope;

    // 合同状态，默认1(有效)
    private String status;

    // 创建人
    private Long createdUser;

    // 创建时间
    private Date createdTime;

    // 修改人
    private Long updateUser;

    // 修改时间
    private Date updateTime;

}
