package com.yy.ppm.system.bean.dto;

import com.yy.ppm.system.bean.po.SysMenuPO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 菜单(SysMenu)DTO
 *
 * @author 张超
 * @date 2021-02-26 15:43:05
 */
@Getter
@Setter
@ToString
public class SysMenuDTO extends SysMenuPO implements Serializable {

    private static final long serialVersionUID = -58892997748413422L;

    /**
     * 是否有子菜单
     */
    private boolean hasChildren;
    /**
     * 状态（0：停用；1：在用）
     */
    private String statusLabel;
    /**
     * 菜单类别（1：菜单；2：目录；3：按钮）
     */
    private String menuTypeLabel;
    /**
     * 菜单ID
     */
    private Long menuId;

}
