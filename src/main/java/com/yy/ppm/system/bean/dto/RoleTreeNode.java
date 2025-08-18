package com.yy.ppm.system.bean.dto;

import com.yy.ppm.common.bean.dto.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 菜单/按钮树数据结构
 **/
@Getter
@Setter
@ToString
public final class RoleTreeNode extends Node {

    private static final long serialVersionUID = 8115915776140996046L;

    public RoleTreeNode() {

    }

    /**
     * 角色编号
     */
    private String roleCd;
    /**
     * 角色名称
     */
    private String roleNm;

}
