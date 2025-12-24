package com.yy.common.util;

import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.auth.bean.dto.UserAuthorizeInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Spring security工具类
 */
@Component
public class SecurityUtils {

    public String getLoginUserAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAuthorizeInfo userAuthorizeInfo = (UserAuthorizeInfo) authentication.getPrincipal();
        return userAuthorizeInfo.getUserIno().getUserAccount();
    }

    public String getLoginUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAuthorizeInfo userAuthorizeInfo = (UserAuthorizeInfo) authentication.getPrincipal();
        return userAuthorizeInfo.getUserIno().getUserName();
    }

    public static Long getLoginUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAuthorizeInfo userAuthorizeInfo = (UserAuthorizeInfo) authentication.getPrincipal();
        return userAuthorizeInfo.getUserIno().getId();
    }

    public UserInfo getUserInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserAuthorizeInfo userAuthorizeInfo = (UserAuthorizeInfo) authentication.getPrincipal();
            return userAuthorizeInfo.getUserIno();
        } catch (Exception e) {
            return null;
        }
    }

    public UserInfo getNewUserInfo() {
        try {
            UserInfo userIno = new UserInfo();
            UserAuthorizeInfo userAuthorizeInfo = new UserAuthorizeInfo(userIno);
            return userAuthorizeInfo.getUserIno();
        } catch (Exception e) {
            return null;
        }
    }

}
