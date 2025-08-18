package com.yy.ppm.finance.bean.dto;

import com.yy.ppm.finance.bean.po.FFeeItemPO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * (FFeeItem)DTO
 *
 * @author 韩旭
 * @date 2021-03-29 11:09:46
 */
@Getter
@Setter
@ToString
public class FFeeItemDTO extends FFeeItemPO implements Serializable {

    private static final long serialVersionUID = -78645124685819669L;

    /*费目类型名称*/
    private String itemTypeNm;
    /*费率*/
    private BigDecimal rate;
    /*费率ID*/
    private Long rateId;
    //商品和服务税收分类合并编码
    private String productCode;

}