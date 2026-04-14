package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaterialWarningConfigPO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author FanQi
 * @data 2026/4/14 11:30
 * @version 1.0
 * @Description 物资预警配置批量DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EMaterialWarningConfigBatchDTO extends EMaterialWarningConfigPO {

    private static final long serialVersionUID = 1L;

    /**
     * 批量新增时使用
     */
    private List<Long> materialIds;
}
