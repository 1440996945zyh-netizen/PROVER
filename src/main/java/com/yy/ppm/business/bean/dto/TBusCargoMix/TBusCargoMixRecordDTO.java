package com.yy.ppm.business.bean.dto.TBusCargoMix;

import com.yy.ppm.business.bean.po.TBusCargoMixDetailPO;
import com.yy.ppm.business.bean.po.TBusCargoMixRecordPO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 票货混配记录
 *
 * @author linqi
 * @since 2024-03-04 16:31:45
 */
@Setter
@Getter
public class TBusCargoMixRecordDTO extends TBusCargoMixRecordPO {

    /**
     * 票货混配明细
     */
    @NotEmpty(message = "明细不能为空")
    private List<TBusCargoMixDetailPO> details;

    /**
     * 新票货号
     */
    private String cargoInfoNo;

    /**
     * 库场名称
     */
    private String storehouseName;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 垛位名称
     */
    private String massName;

    /**
     * 状态Label
     */
    private String statusLabel;

    /**
     * 是否生成混配杂项费
     */
    private String isBilling;
}
