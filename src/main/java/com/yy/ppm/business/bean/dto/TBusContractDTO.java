package com.yy.ppm.business.bean.dto;


import com.yy.ppm.business.bean.dto.contract.TBusTrateDTO;
import com.yy.ppm.business.bean.po.TBusContractCustomerPO;
import com.yy.ppm.business.bean.po.TBusContractPO;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 合同(TBusContract)DTO
 * @Description
 * @createTime 2023年06月29日 10:48:00
 */
@Data
public class TBusContractDTO extends TBusContractPO {

    private static final long serialVersionUID = -35785517457272995L;

    /**
     * 作业公司
     */
    private List<TBusContractCompanyDTO> companyList;

    /**
     * 费率
     */
    @NotEmpty(message = "费率不能为空")
    private List<TBusContractRateDTO> rateList;

    /**
     * 附件信息
     */
    private List<Long> fileIds;
    /**
     * 合同附件
     */
    private List<Long> file02Ids;

    /**
     * 合同类型名称
     */
    private String contractTypeLabel;

    /**
     * 有效起止日
     */
    private String startEndTime;

    /**
     * 状态名称
     */
    private String statusLabel;

    private Boolean hasChild;

    /**
     * 客户
     */
    private List<TBusContractCustomerPO> customers;

    /**
     * 货物编码
     */
    private List<String> cargoCodes;

    /**
     * 货物名称
     */
    private String cargoName;
    /**
     * 是否子合同匹配
     */
    private String isSubMatch;
    private String isSubMatch0;
    private String isSubMatch1;
    private String isSubMatch2;
    private String isSubMatch3;
    private String isSubMatch4;


    //费率ID
    private Long tbcrId;

    private String hasContractFile;
}
