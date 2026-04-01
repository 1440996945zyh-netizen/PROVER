package com.yy.ppm.auth.service.impl;

import com.yy.common.enums.CommonEnum;
import com.yy.common.enums.RedisEnum;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.*;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.auth.mapper.AuthMapper;
import com.yy.ppm.auth.service.AuthService;
import com.yy.ppm.auth.service.LoginService;
import com.yy.ppm.auth.service.UserCacheService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 认证、授权用Service
 */
@Service
public class AuthServiceImpl implements AuthService {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(AuthServiceImpl.class);

    @Resource
    AuthMapper authMapper;

    private final UserCacheService userCacheService;
    private final SecurityUtils securityUtils;
    public AuthServiceImpl(
            UserCacheService userCacheService,
            SecurityUtils securityUtils
    ) {
        this.userCacheService = userCacheService;
        this.securityUtils = securityUtils;
    }

    @Override
    public UserInfo verifyAcc(String accNo, String accPwd, String ip, long uqMark) {
        final String methodName = "AuthServiceImpl:verifyAcc";
        LOGGER.enter(methodName, "登录用户验证");

        UserInfo account = authMapper.getUserInfoByAccount(accNo);

        // admin : admin123
//        String decrypt = JasyptUtils.decrypt(account.getPasswd());
        if (account == null || !accPwd.equals(JasyptUtils.decrypt(account.getPasswd()))) {
            LOGGER.warn("账号或密码错误~");
            throw new BusinessRuntimeException("账号或密码错误~");
        }

        // 停用的场合
        if (CommonEnum.IsUsed.UNUSED.getCode().equals(account.getStatus().toString())) {
            LOGGER.warn("账户已停用~");
            throw new BusinessRuntimeException("账户已停用~");
        }

//        account.setLoginIp(ip);

        // 重置缓存
        userCacheService.cleanCacheByAccNo(account.getUserAccount());

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return account;
    }

    /**
     *根据用户ID获取用户角色信息
     */
    @Override
    public List<String> getUserRoleById(Long id) {
        final String methodName = "AuthServiceImpl:getUserRoleById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        List<String> roles = authMapper.getUserRoleById(id);

        LOGGER.exit(methodName + "[end]", "roles:" + roles);
        return roles;
    }

    @Override
    public List<String> getUserPermissionById(Long id) {
        final String methodName = "getUserPermissionById";
        LOGGER.enter("SysUserServiceImpl:" + methodName + "[start]", "id:" + id);

        List<String> permissions = authMapper.getUserPermissionById(id);

        LOGGER.exit("SysUserServiceImpl:" + methodName + "[end]", "permissions:" + permissions);
        return permissions;
    }

    @Override
    public Map<String, Object> getUserPermissionAndRole() {

        String methodName = "SysUserServiceImpl:getUserPermissionAndRole";
        LOGGER.enter(methodName, "业务执行");

        UserInfo userInfo = getUserInfoByAccount(securityUtils.getUserInfo().getUserAccount(), securityUtils.getUserInfo().getIsSuperadmin());

        if (userInfo == null || (userInfo.getRoleList() == null && userInfo.getPermissions() == null)) {
            throw new BusinessRuntimeException("未查询到用户的权限或角色信息！");
        }

        HashMap<String, Object> authorizeInfo = new HashMap<String, Object>();
        authorizeInfo.put("userId", userInfo.getId());
        authorizeInfo.put("userAccount", userInfo.getUserAccount());
        authorizeInfo.put("userName", userInfo.getUserName());
        authorizeInfo.put("deptName", userInfo.getDeptName());
        authorizeInfo.put("posts", userInfo.getPostName());
        // 配合前端 超级管理员固定加角色
        if (CommonEnum.YesNoMode.YES.getCode().equals(securityUtils.getUserInfo().getIsSuperadmin())) {
            if (userInfo.getRoles() == null || userInfo.getRoles().size() == 0) {
                userInfo.getRoles().add("admin");
            }
        }
        authorizeInfo.put("roles", userInfo.getRoles());
        authorizeInfo.put("permissions", userInfo.getPermissions());

        return authorizeInfo;

    }

    @Override
    public UserInfo getUserInfoByAccount(String account, String isSuperadmin) {
        final String methodName = "SysUserServiceImpl:getUserInfoByAccount";
        LOGGER.enter(methodName + "[start]", "account:" + account);

        boolean isAdmin = CommonEnum.YesNoMode.YES.getCode().equals(isSuperadmin);

        //先从redis缓存中拿用户信息 没有的话从数据库中拿
        return LazyUtils.get(() -> {
            String accStr = userCacheService.getCacheById(RedisEnum.USER_INFO.getCode() + account);
            LOGGER.info("缓存查询用户信息, account: " + accStr);
            if (StringUtils.isNotBlank(accStr)) {
                UserInfo accountDTO = JSONUtils.NON_NULL.toJavaObject(accStr, UserInfo.class);
                // 没有角色和授权重新查询数据库
                if (accountDTO.getId() == null
                        || accountDTO.getPermissions() == null
                        || accountDTO.getPermissions().size() == 0) {
                    return null;
                }
                LOGGER.info("account: " + account);
                LOGGER.exit(methodName, StringUtils.EMPTY);
                return accountDTO;
            } else {
                return null;
            }
        }, () -> {

            UserInfo accountDto = authMapper.getUserInfoByAccount(account);

            // 查询用户角色
            List<HashMap<String, Object>> roleList = authMapper.getUserRoleListById(accountDto.getId());
            // 角色编号列表
            List<String> roles = new ArrayList<>();
            // 设置角色-菜单
            for(HashMap<String, Object> role : roleList){
                // 查询角色菜单权限
                role.put("permissions", authMapper.getRoleMenuByRoleId(StringUtil.getLong(role.get("id"))));
                roles.add(StringUtil.getString(role.get("roleCode")));
            }
            accountDto.setRoleList(roleList);
            accountDto.setRoles(roles);

            // 管理员查看全部
            if (isAdmin) {
                accountDto.setPermissions(authMapper.getAllUserPermission());
            } else {
                accountDto.setPermissions(this.getUserPermissionById(accountDto.getId()));
            }

            userCacheService.setCache(RedisEnum.USER_INFO.getCode() + account, JSONUtils.NON_NULL.toJSONString(accountDto));

            LOGGER.exit(methodName + "[end]", "accountDto:" + accountDto);
            return accountDto;
        });
    }
}
