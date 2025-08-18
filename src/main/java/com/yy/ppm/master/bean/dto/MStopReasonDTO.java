package com.yy.ppm.master.bean.dto;


import com.yy.ppm.master.bean.po.MStopReasonPO;
import lombok.Data;

/**
 * @ClassName 船舶停时原因维护(MStopReason)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月05日 17:21:00
 */
@Data
public class MStopReasonDTO extends MStopReasonPO {

    private static final long serialVersionUID = -23096367580832574L;

    private String stopReasonClassLabel;

    private String stopReasonTypeLabel;

}
