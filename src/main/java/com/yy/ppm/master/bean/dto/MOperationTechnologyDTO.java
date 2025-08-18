package com.yy.ppm.master.bean.dto;

import com.yy.ppm.master.bean.po.MOperationTechnologyMachinePO;
import com.yy.ppm.master.bean.po.MOperationTechnologyPO;
import com.yy.ppm.master.bean.po.MOperationTechnologyWorkerPO;
import com.yy.ppm.master.bean.po.MOperationTechnologyWorkwarPO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 作业工艺po
 * @author yangcl*/
@Data
public class MOperationTechnologyDTO extends MOperationTechnologyPO implements Serializable {
    /**
     * 机械列表
     * */
    private List<MOperationTechnologyMachineDTO> listMachine;
    /**
     * 工人列表
     * */
    private List<MOperationTechnologyWorkerPO> listWorker;
    /**
     * 工属具列表
     * */
    private List<MOperationTechnologyWorkwarDTO> listWorkwar;

    private static final long serialVersionUID = 1L;
}

