package com.yy.ppm.business.bean.dto.cargoInfo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-04 10:36
 */
@Setter
@Getter
public class CleanAllPortStorageDTO {

    /**
     * 票货ID
     */
    @NotNull(message = "票货ID不能为空")
    private Long cargoInfoId;

    /**
     * 作业日期
     */
    @NotNull(message = "作业日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date workDate;

    /**
     * 作业班次编码
     */
    @NotBlank(message = "作业班次编码不能为空")
    private String classCode;

    /**
     * 作业班次名称
     */
    @NotBlank(message = "作业班次名称不能为空")
    private String className;
}
