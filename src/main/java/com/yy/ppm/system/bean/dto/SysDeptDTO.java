package com.yy.ppm.system.bean.dto;


import com.yy.ppm.system.bean.po.SysDeptPO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 部门用DTO
 */
@Getter
@Setter
@ToString
public class SysDeptDTO extends SysDeptPO implements Serializable {

    /** 是否有子菜单 */
    private boolean hasChildren;

    /** 子部门数量 */
    private Integer childCount;

    /** 部门类型 */
    private String deptLevelLabel;

    /** 父编号 */
    private String parentCd;

    /** 父名称 */
    private String parentName;

    /** 是否劳务 */
    private String isLabourLabel;

    /** 内外部 */
    private String inOutTypeLabel;

    private List<SysDeptDTO> childrenList;

    private String value;

    private String label;
}
