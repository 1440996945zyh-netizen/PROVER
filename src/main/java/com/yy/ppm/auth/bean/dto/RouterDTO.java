package com.yy.ppm.auth.bean.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author FanQi
 * @version 1.0
 * @date 2023/4/21 14:55
 */
@Data
public class RouterDTO {
private Long id;

    private List<RouterDTO> children;
    private String name;
    private boolean alwaysShow;

    private String path;


    /**当设置 true 的时候该路由不会在侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
     * */
    private Boolean hidden;

    /**当设置 noRedirect 的时候该路由在面包屑导航中不可被点击 */
    private String redirect;

    private String component;

    private String icon;

    private String query;

    private String menuIconColor;

    private HashMap<String, Object> meta;

    /**是否为外链（0是 1否） */
    private Long isFrame;
    /**外链地址 */
    private String link;
    private String isQuickEnter;
}
