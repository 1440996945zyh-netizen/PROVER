package com.yy.ppm.finance.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * (FFeeItem)PO
 *
 * @author 韩旭
 * @date 2021-03-29 11:09:46
 */
@Getter
@Setter
@ToString
public class FFeeItemPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -88175221488438464L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 费目编号
     */
    private String itemCd;
    /**
     * 费目名称
     */
    private String itemNm;
    /**
     * 费目类型 字典 FEE_ITEM_TYPE 船方，货方，杂项
     */
    private String itemTypeCd;

    /**
     * 费用类型 字典 RATE_TYPE
     */
    private String rateTypeCd;

    /**
     * 费用类型 字典 RATE_TYPE
     */
    private String rateTypeNm;
    /**
     * 排序号
     */
    private Integer sortNum;
    /**
     * 备注
     */
    private String remark;

    /**
     * 是否主营收入 0否/1是
     */
    private String isMainIncome;

    private String isLease;
}