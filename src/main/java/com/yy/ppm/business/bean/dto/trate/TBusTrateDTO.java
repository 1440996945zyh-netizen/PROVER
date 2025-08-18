package com.yy.ppm.business.bean.dto.trate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "主键ID不能为空")
    private Long id;

    /**
     * 合同
     */
    @NotEmpty(message = "合同不能为空")
    private List<TBusTrateContractDTO> contracts;

    /**
     * 客户
     */
    @NotEmpty(message = "客户不能为空")
    private List<TBusTrateCustomerDTO> customers;

    /**
     * 有效期起
     */
    @NotNull(message = "有效期起不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;

    /**
     * 有效期止
     */
    @NotNull(message = "有效期止不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;

    /**
     * 条目
     */
    @NotEmpty(message = "条目不能为空")
    private List<TBusTrateItemDTO> items;

    /**
     * 状态 10待发布/20已发布
     */
    private String status;

    /**
     * 合同编号，多个拼接
     */
    private String contractNos;

    /**
     * 客户名称，多个拼接
     */
    private String customerNames;
}
