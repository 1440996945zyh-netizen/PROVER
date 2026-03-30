package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.MEpatrolStandardPO;
import com.yy.ppm.equipment.bean.po.MEpatrolStandardSubPO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 巡检标准
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MEpatrolStandardDTO extends MEpatrolStandardPO {

    private static final long serialVersionUID = 1L;

    /** 子表 */
    private List<MEpatrolStandardSubPO> subList;
}