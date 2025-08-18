package com.yy.ppm.business.bean.dto.assignFleet;

import com.yy.ppm.business.bean.po.TBusAssignFleetPO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-10 15:27
 */
@Setter
@Getter
public class TBusAssignFleetDTO extends TBusAssignFleetPO {

    private List<Long> tbttrIds;
}
