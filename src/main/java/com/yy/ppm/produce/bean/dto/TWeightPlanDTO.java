package com.yy.ppm.produce.bean.dto;


import com.yy.ppm.master.bean.po.MMachineTypeModelPO;
import com.yy.ppm.produce.bean.po.TWeightPlanItemPO;
import com.yy.ppm.produce.bean.po.TWeightPlanPO;
import lombok.Data;

import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 杂项过磅计划表(TWeightPlan)DTO
 * @Description
 * @createTime 2023年12月05日 08:39:00
 */
@Data
public class TWeightPlanDTO extends TWeightPlanPO {

    private static final long serialVersionUID = -73458188507975051L;

    /**
     * 车辆安排
     * */
    private List<TWeightPlanItemDTO> list;

    /**
     * 是否过磅
     * */
    private Long isPound;
}
