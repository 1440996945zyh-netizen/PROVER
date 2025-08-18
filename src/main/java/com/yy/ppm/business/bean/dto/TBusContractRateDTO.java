package com.yy.ppm.business.bean.dto;


import com.yy.ppm.business.bean.dto.contract.TBusTrateDTO;
import com.yy.ppm.business.bean.po.TBusContractRatePO;
import lombok.Data;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 合同费率表(TBusContractRate)DTO
 * @Description
 * @createTime 2023年06月29日 10:49:00
 */
@Data
public class TBusContractRateDTO extends TBusContractRatePO {

    private static final long serialVersionUID = -24928180642526585L;

    private TBusTrateDTO trate;
}
