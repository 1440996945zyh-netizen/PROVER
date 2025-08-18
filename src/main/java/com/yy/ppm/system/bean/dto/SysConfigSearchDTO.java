package com.yy.ppm.system.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用户信息查询DTO
 * @author 张超
 * @date 2021年2月19日14:13:03
 */
@Getter
@Setter
@ToString
public class SysConfigSearchDTO extends PageParameter implements Serializable {
    private static final long serialVersionUID = 3803991192565776724L;
    /**参数名称 */
    public String configName;
    /**参数键名 */
    public String configKey;
}
