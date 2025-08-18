package com.yy.ppm.master.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 字典
 *
 * @author 成龙
 * @date 2021-3-1 15:50:48
 */
@Getter
@Setter
@ToString
public class MDictDataPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -1101726927330759376L;
    /**
     * 主键ID*/
    private Long id;
    /**
     * 类型CD*/
    @NotEmpty
    private String dictType;
    /**
     * 字典cd*/
    @NotEmpty
    private String dictValue;
    /**
     * 字典值*/
    @NotEmpty
    private String dictLabel;
    /**
     * 排序号*/
    @NotNull
    private Integer sortNum;
    /**
     * 状态*/
    private String status;
    /**
     * 备注*/
    private String remark;
    /**
     * 备注*/
    private String dictEngLabel;
}
