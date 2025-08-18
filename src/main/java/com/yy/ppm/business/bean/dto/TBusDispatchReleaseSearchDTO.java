package com.yy.ppm.business.bean.dto;


import com.yy.common.page.PageParameter;
import com.yy.common.util.str.StringUtil;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 放行单表(TBusDispatchRelease)SearchDTO
 * @Description
 * @createTime 2024年04月16日 16:03:00
 */
@Data
public class TBusDispatchReleaseSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -55069639006781906L;

    /**
     * 内外贸
     */
    private String tradeType;
    /**
     * 客户id
     */
    private String customerId;
    /**
     * 船舶状态
     */
    private String shipStatusCode;
    /**
     * 进出口
     */
    private String impExp;
    /**
     * 装卸
     */
    private String loadUnload;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 航次id
     */
    private Long shipvoyageId;
    /**
     * 航次子表id
     */
    private Long shipvoyageItemId;
    /**
     * 放行单号
     */
    private String deliveryNumbers;
    /**
     * 数量
     */
    private Long quantity;
    /**
     * 吨
     */
    private BigDecimal ton;
    /**
     * 包装code
     */
    private String packingCode;
    /**
     * 包装name
     */
    private String packingName;
    /**
     * 规格
     */
    private String specs;
    /**
     * 是否放行
     */
    private String permitThrough;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建人
     */
    private String createByName;
    /**
     * 更新人
     */
    private String updateByName;

    private List<String> deliveryList;

    public void setDeliveryNumbers(String deliveryNumbers) {
        this.deliveryNumbers = deliveryNumbers;
        if(StringUtil.isNotEmpty(this.deliveryNumbers) && CollectionUtils.isEmpty(this.deliveryList)){
            this.deliveryNumbers = this.deliveryNumbers.replaceAll(",","，");
            List<String> list = Arrays.asList(this.deliveryNumbers.split("，"));
            this.deliveryList = list;
        }
    }
    private String shipName;

    private String voyage;
}

