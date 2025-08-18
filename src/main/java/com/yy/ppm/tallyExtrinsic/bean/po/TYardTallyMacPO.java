package com.yy.ppm.tallyExtrinsic.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * App理货(TYardTallyMacPO)PO
 * @author chenfs
 * @date 2023-09-15
 */

@Getter
@Setter
@ToString
public class TYardTallyMacPO extends BasePO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tallyId;
    @NotNull(message = "计划ID不能为空")
    private Long planId;
    @NotNull(message = "车号不能为空")
    private String macName;
    private String status;
    @NotNull(message = "门机不能为空")
    private String doorName;
    private Integer quantity;

    private String processCode;

    private String planNo;
    private String goodsName;
    private String confirmStatus;
    private String storageYardNm;
    private Long reservationPoundId;
}


