package com.yy.ppm.finance.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * (FFeeItem)SearchDTO
 *
 * @author 韩旭
 * @date 2021-03-29 11:09:46
 */
@Getter
@Setter
@ToString
public class FFeeItemSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -99656069229309002L;

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
     * 费目类型CD 字典 RATE_TYPE
     */
    private String rateTypeCd;

    /**
     * 费目类型名称 字典 RATE_TYPE
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
     * 费率
     */
    private BigDecimal rate;
}
