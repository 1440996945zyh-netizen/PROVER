package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.PatrolStandardPO;
import com.yy.ppm.equipment.bean.po.PatrolStandardSubPO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 巡检标准
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PatrolStandardDTO extends PatrolStandardPO {

    private static final long serialVersionUID = 1L;

    /** 子表 */
    private List<PatrolStandardSubPO> subList;
}