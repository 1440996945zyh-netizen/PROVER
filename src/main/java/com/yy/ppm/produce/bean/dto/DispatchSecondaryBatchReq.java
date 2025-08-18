package com.yy.ppm.produce.bean.dto;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class DispatchSecondaryBatchReq extends BasePO {
    List<TPrdDispatchSecondaryDTO> dispatchSecondaryList;
    List<Long> workPlanIds;
    String dispatchType;
    String laborStatus;
}
