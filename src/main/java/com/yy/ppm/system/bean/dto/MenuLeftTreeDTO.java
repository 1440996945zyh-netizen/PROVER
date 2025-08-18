package com.yy.ppm.system.bean.dto;

import com.yy.ppm.auth.bean.dto.RouterDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

/**
 * @author FanQi
 * @version 1.0
 * @date 2023/4/21 14:55
 */
@Data
public class MenuLeftTreeDTO{
private Long id;
    private List<MenuLeftTreeDTO> children;
    private String name;
    private boolean alwaysShow;

    private String path;


    /**当设置 true 的时候该路由不会在侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
     * */
    private Boolean hidden;

    /**当设置 noRedirect 的时候该路由在面包屑导航中不可被点击 */
    private String redirect;

    private String component;

    private HashMap<String, Object> meta;

    private String icon;


}
