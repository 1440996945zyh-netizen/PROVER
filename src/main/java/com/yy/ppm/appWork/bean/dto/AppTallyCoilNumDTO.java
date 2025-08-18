package com.yy.ppm.appWork.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.appWork.bean.po.TYardTallyItemPO;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * 港存(AppTallyCoilNumDTO)PO
 *
 * @author chenfs
 * @date 2023-10-24 8:53:36
 */

@Getter
@Setter
@ToString
public class AppTallyCoilNumDTO extends BasePO {
    /**ID*/
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**票货ID*/
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cargoInfoId;
    private List<Long> cargoInfoIds;
    /**理货ID*/
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tallyId;
    /**序号*/
    private String num;
    /**卷钢号*/
    private String coilNum;
    /**状态*/
    private Integer status;
    /*** 尺寸2*/
    private String coilSize2;
    /*** 尺寸1*/
    private String coilSize1;
    /**重量*/
    private BigDecimal ton;
    private String createTimes;
    private String receiveAddress;
    private String workPlanId;

}

