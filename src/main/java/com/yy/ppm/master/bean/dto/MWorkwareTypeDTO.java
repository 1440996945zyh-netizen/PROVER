package com.yy.ppm.master.bean.dto;

import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.master.bean.po.MWorkwareTypeModelPO;
import com.yy.ppm.master.bean.po.MWorkwareTypePO;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 工属具类型DTO
 * */
@Data
public class MWorkwareTypeDTO extends MWorkwareTypePO implements Serializable {

    /**
     * 工属具型号
     * */
    List<MWorkwareTypeModelPO> list;

    /**
     * 型号编号
     */
    private String modelCode;

    /**
     * 型号名称
     */
    private String modelName;

    /**
     * 创建人姓名
     * */
    private String createName;

    /**
     * 修改人姓名
     * */
    private String updateName;

    private static final long serialVersionUID = 1L;
}

