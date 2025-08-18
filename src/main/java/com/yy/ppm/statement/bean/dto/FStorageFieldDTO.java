package com.yy.ppm.statement.bean.dto;

import com.yy.ppm.business.bean.dto.TBusContractDTO;
import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageDTO;
import com.yy.ppm.statement.bean.po.TStorageFeeHisPO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 场存明细DTO
 * @author yangcl
 * */
@ToString
@Getter
@Setter
public class FStorageFieldDTO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1987782015650117085L;
    /**
     * 场存明细*/
    private List<TPrdPortStorageDTO> storageDetailList;
    /**
     * 合同信息集合*/
    private List<TBusContractDTO> contractList;
    /**
     * 历史结算信息*/
    private List<TStorageFeeHisPO> hisList;
    /**
     * 最后一次进场日期*/
    private String lastDate;
}
