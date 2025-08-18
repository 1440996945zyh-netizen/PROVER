package com.yy.ppm.machine.bean.dto;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

@Data
public class TMacWorKLocationDTO extends BasePO {

    private Long weighbridgeId;

    private String location;
}
