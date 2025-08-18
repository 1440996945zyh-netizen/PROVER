package com.yy.ppm.auth.bean.dto;

import com.yy.common.util.str.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用户角色/权限领域对象
 **/
@Getter
@Setter
@ToString
public class UserAuthorizeInfo implements UserDetails {

    private static final long serialVersionUID = -4378661429893697822L;

    public UserAuthorizeInfo(UserInfo userIno) {
        this.userIno =  userIno;
    }

    private UserInfo userIno;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();

        if (userIno != null && userIno.getPermissions() !=null) {

            int permissionLength = userIno.getPermissions().size();

            for(int i = 0; i < permissionLength;i++) {
                if (!StringUtil.isEmpty(userIno.getPermissions().get(i))) {
                    list.add(new SimpleGrantedAuthority(userIno.getPermissions().get(i)));
                }
            }

            return list;
        }

        return null;
    }

    @Override
    public String getPassword() {
        return userIno.getPasswd();
    }

    @Override
    public String getUsername() {
        return userIno.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {          // 账号是否没有过期
        return true;
    }

    /** 账号是否没有被锁定 */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /** 账号的凭证是否没有过期 */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** 账号是否可用 */
    @Override
    public boolean isEnabled() {
        return true;
    }

}
