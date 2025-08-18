package com.yy.ppm.business.bean.dto;


import com.yy.ppm.business.bean.po.TBusRatePO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName 费率(TBusRate)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月03日 16:48:00
 */
@Data
public class TBusRateUpdateDTO {

    private static final long serialVersionUID = -74805729703512435L;
    private TBusRateDTO rows;
    private List<TBusRateDTO> list;

}
