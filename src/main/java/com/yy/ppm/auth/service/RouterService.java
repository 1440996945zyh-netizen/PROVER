package com.yy.ppm.auth.service;

import com.yy.ppm.auth.bean.dto.RouterDTO;

import java.util.List;

/**
 * @author FanQi
 * @version 1.0
 * @date 2023/4/23 16:30
 */
public interface RouterService {

    /**
     * 登录后查询左侧菜单路由
     * @return
     * @param loginType
     */
    List<RouterDTO> getRouters(String loginType);

}
