package com.yy.ppm.system.bean.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 菜单(SysMenu)SearchDTO
 *
 * @author 张超
 * @date 2021-02-26 15:43:07
 */
@Getter
@Setter
@ToString
public class SysMenuSearchDTO implements Serializable {

    private static final long serialVersionUID = -42544105796890544L;

    /**
     * 名称
     */
    private String menuName;
    private String status;
    private String isAdmin;
    private Long userId;

    private Long parentGid;

}
