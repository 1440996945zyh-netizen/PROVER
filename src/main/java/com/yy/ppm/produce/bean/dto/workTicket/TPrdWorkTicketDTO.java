package com.yy.ppm.produce.bean.dto.workTicket;

import com.yy.ppm.produce.bean.po.TPrdWorkTicketLaborPO;
import com.yy.ppm.produce.bean.po.TPrdWorkTicketPO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-15 11:14
 */
@Setter
@Getter
public class TPrdWorkTicketDTO extends TPrdWorkTicketPO {

    /**
     * 作业票明细
     */
    @NotEmpty(message = "作业票明细不能为空")
    private List<TPrdWorkTicketDetailDTO> details;

    /**
     * 作业票劳务
     */
    private List<TPrdWorkTicketLaborPO> labors;

    /**
     * tabs数据
     */
    private List<Map<String,Object>> tableList;
}
