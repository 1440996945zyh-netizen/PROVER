package com.yy.ppm.business.bean.dto.contract;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @Auther linqi
 * @Description 阶梯费率表
 * @Date 2023-11-08 11:33
 */
@Setter
@Getter
public class TBusTrateDTO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 合同
     */
    private List<TBusTrateContractDTO> contracts;

    /**
     * 客户
     */
    private List<TBusTrateCustomerDTO> customers;

    /**
     * 有效期起
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;

    /**
     * 有效期止
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;

    /**
     * 条目
     */
    private List<TBusTrateItemDTO> items;
}
