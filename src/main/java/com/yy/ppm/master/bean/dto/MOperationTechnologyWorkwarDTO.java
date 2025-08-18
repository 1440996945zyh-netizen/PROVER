package com.yy.ppm.master.bean.dto;

import com.yy.ppm.master.bean.po.MOperationTechnologyPO;
import com.yy.ppm.master.bean.po.MOperationTechnologyWorkwarPO;
import lombok.Data;

import java.io.Serializable;

/**
 * 作业工艺工属具配置
 * @author yangcl
 * */
@Data
public class MOperationTechnologyWorkwarDTO extends MOperationTechnologyWorkwarPO implements Serializable {
    /**
     * 工属具类型code
     */
    private String typeName;

    /**
     * 工属具型号code
     */
    private String modelName;

    private static final long serialVersionUID = 1L;
}

