package com.yy.ppm.gis.dto.onSiteDynamics;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-24 15:21
 */
@Setter
@Getter
public class InoutDetailQueryDTO {

    /**
     * 票货号
     */
    @NotNull(message = "票货号不能为空")
    private String cargoInfoNo;

    /**
     * 垛位ID
     */
    @NotNull(message = "垛位ID不能为空")
    private Long massId;

    /**
     * 起始作业日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginWorkDate;

    /**
     * 起始作业班次编码
     */
    private String beginClassCode;

    /**
     * 结束作业日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endWorkDate;

    /**
     * 结束作业班次编码
     */
    private String endClassCode;

    /**
     * 子过程编码
     */
    private String processDetailCode;
}
