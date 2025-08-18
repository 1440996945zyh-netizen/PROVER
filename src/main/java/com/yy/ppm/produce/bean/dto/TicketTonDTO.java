package com.yy.ppm.produce.bean.dto;

import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDetailDTO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Auther chenfs
 * @Description
 * @Date 2023-10-16 14:08
 */
@Setter
@Getter
public class TicketTonDTO {

    /**
     * 百分比
     */
    @NotNull(message = "百分比不能为空")
    private BigDecimal percentage;

    /**
     * 作业票新信息
     */
    private List<TPrdWorkTicketDetailDTO> ticketList;


}
