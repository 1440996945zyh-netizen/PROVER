package com.yy.ppm.master.bean.dto;

import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.master.bean.po.MOperationProcessPO;
import com.yy.ppm.master.bean.po.MOperationSubProcessPO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MOperationProcessDTO extends MOperationProcessPO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String processTypeLabel;

    private String sourceLabel;

    private String destinationLabel;
    /**
     * 子作业过程列表
     * */
    List<MOperationSubProcessDTO> list;

    /**
     * 创建人姓名
     * */
    private String createName;

    /**
     * 修改人姓名
     * */
    private String updateName;

}

