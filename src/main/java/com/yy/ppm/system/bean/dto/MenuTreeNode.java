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
public final class MenuTreeNode extends Node {

    private static final long serialVersionUID = 8115915776140996046L;

    public MenuTreeNode() {

    }

    /**
     * 菜单地址指向名
     */
    private String path;
    /**
     * 菜单标题
     */
    private String title;
    /**
     * 菜单路由
     */
    private String url;
    /**
     * 菜单名称
     */
    private String menuNm;
    /**
     * 菜单图标
     */
    private String menuIcon;
    /**
     * 菜单图标颜色
     */
    private String menuIconColor;
}
