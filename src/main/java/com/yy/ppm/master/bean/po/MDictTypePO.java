package com.yy.ppm.master.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 字典类型
 *
 * @author 成龙
 * @date 2021-3-1 15:50:48
 */
@Getter
@Setter
@ToString
public class MDictTypePO extends BasePO implements Serializable {

    private static final long serialVersionUID = -1101726927330759376L;
    /**
     * 主键ID*/
    private Long id;
    /**
     * 类型CD*/
    @NotEmpty
    private String dictType;
    /**
     * 类型名称*/
    @NotEmpty
    private String dictName;
    /**
     * 是否开放*/
    private String isOpen;
    /**
     * 状态*/
    private String status;
    /**
     * 排序号*/
    private Integer sortNum;
    /**
     * 备注*/
    private String remark;
}
