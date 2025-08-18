package com.yy.ppm.produce.bean.dto;

import com.yy.ppm.produce.bean.po.TPrdOddWorkPlanDetailPO;
import com.yy.ppm.produce.bean.po.TPrdOddWorkPlanPO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Auther wangxd
 * @Description
 * @Date 2023-12-12 10:08
 */
@Setter
@Getter
public class TPrdOddResultDTO extends TPrdOddWorkPlanPO {

    private String statusText;
    /**
     * 计划详情
     */
    private List<TPrdOddWorkPlanDetailPO> workTimeTable;

    private String reportTime;
}
