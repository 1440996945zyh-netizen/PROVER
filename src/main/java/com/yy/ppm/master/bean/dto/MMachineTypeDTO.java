package com.yy.ppm.master.bean.dto;

import com.yy.ppm.master.bean.po.MMachineTypeModelPO;
import com.yy.ppm.master.bean.po.MMachineTypePO;
import lombok.Data;

import java.util.List;


/**
 * 机械类型视图对象 b_machine_type
 *
 */
@Data
public class MMachineTypeDTO extends MMachineTypePO {

    private static final long serialVersionUID = 1L;

    /**
     * 机械型号
     * */
    List<MMachineTypeModelPO> list;

    private String modelCode;

    private String modelName;
}
