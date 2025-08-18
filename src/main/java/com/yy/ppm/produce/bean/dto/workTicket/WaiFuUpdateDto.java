package com.yy.ppm.produce.bean.dto.workTicket;

import lombok.Data;

@Data
public class WaiFuUpdateDto {
    private Long ticketDetailId;

    private String status;

    private String errorMsg;
}
