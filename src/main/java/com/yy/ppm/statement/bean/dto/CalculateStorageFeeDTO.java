package com.yy.ppm.statement.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.util.Date;

/**
 * 堆存费计算DTO
 *
 * @author yangcl
 */
@ToString
@Getter
@Setter
public class CalculateStorageFeeDTO {
    /**
     * 计费开始时间*/
    @NotNull(message = "结算开始时间不可为空")
    private String startDate;
    /**
     * 计费结束时间*/
    @NotNull(message = "结算结束时间不可为空")
    private String endDate;
    /**
     * 合同GID*/
    private Long contractId;
    /**
     * 票货GID*/
    @NotNull(message = "票货ID不可为空")
    private Long cargoInfoId;

    @NotNull(message = "货物code不可为空")
    private String cargoCode;

    /**
     * 历史结算GID*/
    private Long historyGid;
}
