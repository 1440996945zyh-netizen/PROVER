package com.yy.ppm.tallyExtrinsic.bean.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 前沿确认
 * @Description
 * @createTime 2024年06月13日 10:48:00
 */
@Data
public class TBusReservationConfirmSearchDTO implements Serializable {

    private static final long serialVersionUID = -95785237457272995L;

    private String macNo;

    private String status;

    private Long id;

    private String workType;

}
