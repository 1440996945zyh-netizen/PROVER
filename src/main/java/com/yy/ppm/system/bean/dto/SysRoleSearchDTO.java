package com.yy.ppm.system.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 角色(SysRole)SearchDTO
 *
 * @author 张超
 * @date 2021-03-02 09:34:10
 */
@Getter
@Setter
@ToString
public class SysRoleSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 554380049879634681L;

    /**
     * 权限标识
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 状态（0：停用；1：在用）
     */
    private String status;

    /**
     * 角色类
     */
    private String roleClass;

}
