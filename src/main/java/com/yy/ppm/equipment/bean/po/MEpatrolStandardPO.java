package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 巡检标准主表
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MEpatrolStandardPO extends BasePO {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 标准编号 */
    private String standardCode;

    /** 标准名称 */
    private String standardName;

    /** 设备ID */
    private String eqptId;
}
