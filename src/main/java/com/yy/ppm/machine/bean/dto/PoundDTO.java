package com.yy.ppm.machine.bean.dto;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.util.Date;

@Data
public class PoundDTO extends BasePO {
    private Long noteId;
    private Date weighInDt;
    private Date weighOutDt;
    private String isFinished;
}
