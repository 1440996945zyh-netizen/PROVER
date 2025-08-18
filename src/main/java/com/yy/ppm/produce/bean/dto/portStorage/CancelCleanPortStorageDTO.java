package com.yy.ppm.produce.bean.dto.portStorage;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-04 10:36
 */
@Setter
@Getter
public class CancelCleanPortStorageDTO {

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
}
