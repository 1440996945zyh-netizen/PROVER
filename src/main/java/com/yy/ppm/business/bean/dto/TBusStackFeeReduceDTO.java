package com.yy.ppm.business.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class TBusStackFeeReduceDTO extends BasePO  implements Serializable {
    private String remark;
    private Integer reduceDays;
    private String reduceType;
    private Long cargoInfoId;
    private Long id;
    private String reduceTypeLabel;

    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date reduceEndDate;
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date calEndDate;

    private List<Long> fileList;
}
