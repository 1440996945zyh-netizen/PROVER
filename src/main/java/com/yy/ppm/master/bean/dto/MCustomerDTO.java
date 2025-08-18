package com.yy.ppm.master.bean.dto;


import com.yy.ppm.master.bean.po.MCustomerPO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName 客户资料(MCustomer)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月05日 16:27:00
 */
@Data
public class MCustomerDTO extends MCustomerPO {

    private static final long serialVersionUID = 307572250679569013L;

    private List<MCustomerInvoiceDTO> invoceList;

    private List<MCustomerTypeDTO> typeList;

}
