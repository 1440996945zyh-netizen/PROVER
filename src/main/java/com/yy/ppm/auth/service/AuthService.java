package com.yy.ppm.auth.service;


import com.yy.ppm.auth.bean.dto.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * 认证、授权用Service
 */
public interface AuthService {

    /**
     * 登录验证
     *
     * @param accNo  账号
     * @param accPwd 密码
     * @param ip     登录ip
     * @param uqMark 登录标记
     * @return Account账号信息
     **/
    UserInfo verifyAcc(String accNo, String accPwd, String ip, long uqMark);

    /**
     * 根据用户ID获取用户权限信息
     */
    public List<String> getUserRoleById(Long id);

    /**
     * 根据用户ID获取用户权限按钮信息
     */
    public List<String> getUserPermissionById(Long id);

    /**
     * 根据用户ID获取用户权限及角色信息
     */
    public Map<String, Object> getUserPermissionAndRole();

    /**
     * 登录时根据用户账号查询用户信息
     */
    public UserInfo getUserInfoByAccount(String account, String isSuperadmin);
}
