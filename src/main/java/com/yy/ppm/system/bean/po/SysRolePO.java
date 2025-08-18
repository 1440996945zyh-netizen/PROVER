package com.yy.ppm.system.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 角色(SysRole)PO
 *
 * @author 张超
 * @date 2021-03-02 09:34:08
 */
@Getter
@Setter
@ToString
public class SysRolePO extends BasePO implements Serializable {

    private static final long serialVersionUID = -55450949367571418L;

    /**ID */
    private Long id;
    /**组织架构ID */
    private Long deptId;
    /**角色名称 */
    private String roleCode;
    /**角色名称 */
    private String roleName;
    /**角色状态(0:不可用，1:可用) */
    private String status;
    /**是否删除(0:否，1:是) */
    private String deleted;
    /** 排序*/
    private Long roleSort;
    /** 菜单树选择项是否关联显示*/
    private Boolean menuCheckStrictly;
    /** 部门树选择项是否关联显示*/
    private Boolean deptCheckStrictly;
    /** 备注*/
    private String remark;
    /** 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）*/
    private String dataScope;
    /** 角色分类*/
    private String roleClass;
    /** 部门名称*/
    private String deptName;
}
