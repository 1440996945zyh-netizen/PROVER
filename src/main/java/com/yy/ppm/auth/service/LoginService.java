package com.yy.ppm.auth.service;

import com.yy.ppm.auth.bean.dto.UserInfo;

/**
 * 用户登录Service
 */
public interface LoginService {

    /**
     * 登录信息
     * @param accountDTO
     * @return
     */
    UserInfo login(UserInfo accountDTO);


}
