package com.yy.ppm.produce.bean.dto.portStorage;

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
     * 票货ID
     */
    @NotNull(message = "票货ID不能为空")
    private Long cargoInfoId;

    /**
     * 库场ID
     */
    @NotNull(message = "库场ID不能为空")
    private Long storehouseId;

    /**
     * 区域ID
     */
    @NotNull(message = "区域ID不能为空")
    private Long regionId;

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
    /**
     * 出入库
     */
    private String inoutType;
}
