package com.yy.ppm.system.bean.po;


import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 菜单(SysMenu)PO
 *
 * @author 张超
 * @date 2021-02-26 15:43:04
 */
@Getter
@Setter
@ToString
public class SysMenuPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -92907673298062384L;

    /**菜单ID */
    private Long id;
    /**菜单名称 */
    private String menuName;
    /**父菜单ID */
    private Long parentId;
    /**显示顺序 */
    private Long orderNum;
    /**路由地址 */
    private String path;
    /**组件路径 */
    private String component;
    /**路由参数 */
    private String query;
    /**app图标 */
    private String iconApp;
    /**是否外部打开（0是 1否） */
    private Long isFrame;
    /** 外链地址 */
    private String link;
    /**是否缓存（0缓存 1不缓存） */
    private Long isCache;
    /**菜单类型（M目录 C菜单 F按钮） */
    private String menuType;
    /**菜单状态（0显示 1隐藏） */
    private String visible;
    /**菜单状态（0正常 1停用） */
    private String status;
    /**权限标识 */
    private String perms;
    /**菜单图标 */
    private String icon;
    /**备注 */
    private String remark;
    /**图标颜色 */
    private String menuIconColor;
    /**数据类别（1：PC；2：APP) */
    private String dataType;

    private String appType;


}
