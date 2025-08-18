package com.yy.ppm.statement.bean.dto.bizCostStatement;

import com.yy.ppm.business.bean.po.TBusContractPO;
import com.yy.ppm.business.bean.po.TBusContractRatePO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-18 15:08
 */
@Setter
@Getter
public class TBusContractDTO extends TBusContractPO {

    private List<TBusContractRatePO> rates;

    private TBusTrateItemDTO item;

    /**
     * tcsId
     */
    private Long tcsId;
}
