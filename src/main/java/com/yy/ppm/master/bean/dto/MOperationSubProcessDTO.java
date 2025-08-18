package com.yy.ppm.master.bean.dto;

import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.master.bean.po.MOperationSubProcessPO;
import lombok.Data;

import java.io.Serializable;

@Data
public class MOperationSubProcessDTO extends MOperationSubProcessPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 创建人姓名
     * */
    private String createName;

    /**
     * 修改人姓名
     * */
    private String updateName;

    private String sourceLabel;

    private String destinationLabel;
}

