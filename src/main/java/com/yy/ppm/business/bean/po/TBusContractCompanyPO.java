package com.yy.ppm.business.bean.po;


import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName (TBusContractCompany)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 11:57:00
 */
@Data
public class TBusContractCompanyPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 457374221740678001L;

        /** 主键ID */
    private Long id;
            /** 合同ID */
    private Long contractId;
            /** 作业公司id */
    private Long companyId;
            /** 作业公司名称 */
    private String companyName;

}

