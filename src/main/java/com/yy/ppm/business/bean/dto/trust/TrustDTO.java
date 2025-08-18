package com.yy.ppm.business.bean.dto.trust;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.business.bean.dto.TBusTrustCargoDTO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-10-18 21:37
 */
@Setter
@Getter
public class TrustDTO {

    @NotNull(message = "指令ID不能为空")
    private Long id;

    private Long planQuantity;

    private BigDecimal planTon;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date endTime;

    private String isWeiqiaoPoundRemark;

    private String poundRemark;

    private List<Long> fileIds;

    private List<TrustCargoDTO> updates;

    private List<TBusTrustCargoDTO> inserts;

    /**
     * 集疏港类型
     */
    private String type;
    //转水前船名
    private String preChangeShipName;

    //转水前编号
    private String preChangeShipNo;

    private BigDecimal estAmount;


}
