package com.yy.ppm.system.bean.dto;

import com.yy.ppm.system.bean.po.SysRolePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 角色(SysRole)DTO
 *
 * @author 张超
 * @date 2021-03-02 09:34:09
 */
@Getter
@Setter
@ToString
public class SysRoleDTO extends SysRolePO implements Serializable {

    private static final long serialVersionUID = -79580058524679175L;

    /**
     * 状态label（0：停用；1：在用）
     */
    private String statusLabel;
    /** 菜单List */
    private List<Long> menuIds;
    /** 部门List */
    private List<Long> deptIds;

    /**
     * 角色功能权限
     */
    private Set<String> permissions;
}
