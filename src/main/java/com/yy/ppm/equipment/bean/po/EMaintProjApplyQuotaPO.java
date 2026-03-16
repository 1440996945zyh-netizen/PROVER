package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 维修项目申请定额子表
 * @author system
 */
@Getter
@Setter
@ToString
public class EMaintProjApplyQuotaPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;
    // 主键ID
    private Long id;

    // 维修项目申请ID
    private Long parentId;

    // 定额编号
    private String quotaCode;

    // 维修项目名称
    private String projectName;

    // 维修项目内容
    private String projectContent;

    // 计量单位
    private String unit;

    // 不含税金额
    private BigDecimal amountExcludingTax;

    // 税率
    private BigDecimal taxRate;

    // 含税金额
    private BigDecimal amountIncludingTax;

    private int projectNum;

    // 创建人
    private Long createdUser;

    // 创建时间
    private Date createdTime;
    // 修改人
    private Long updateUser;
    // 修改时间
    private Date updateTime;

}
