package com.yy.ppm.produce.bean.dto;

import com.yy.ppm.produce.bean.po.TPrdOddWorkPlanLogPO;
import com.yy.ppm.produce.bean.po.TPrdOddWorkPlanPO;
import lombok.Getter;
import lombok.Setter;

/**
 * @Auther wangxd
 * @Description
 * @Date 2023-12-12 10:08
 */
@Setter
@Getter
public class TPrdOddLogResultDTO extends TPrdOddWorkPlanLogPO {

    private String statusText;

    private String operateContent;
}
