package com.yy.ppm.dispatch.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TDisLowerDoorPO extends BasePO {
   private List<TDisLowerCabinPO> handDoorList;
   private String flag;
   private Long shipvoyageId;
}
