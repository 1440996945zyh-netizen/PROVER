package com.yy.ppm.auth.mapper;


import com.yy.ppm.auth.bean.dto.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 认证、授权用Dao
 */
@Mapper
@Repository
public interface AuthMapper {

    /**
     * 根据用户ID获取用户角色信息
     */
    public List<String> getUserRoleById(Long id);

    /**
     * 根据用户ID获取用户角色信息
     */
    public List<HashMap<String, Object>> getUserRoleListById(Long id);

    /**
     * 获取全部用户角色信息
     */
    public List<String> getAllUserRole();

    /**
     * 获取全部用户角色信息
     */
    public List<String> getUserRoleRegist(String roleCd);

    /**
     * 根据用户ID获取用户权限信息
     */
    public List<String> getUserPermissionById(Long id);

    /**
     * 获取全部用户权限信息
     */
    public List<String> getAllUserPermission();


    /**
     * 根据用户ID获取用户权限信息
     */
    public List<String> getUserPermissionRegist(String roleCd);

    /**
     * 根据用户账号获取用户基础信息
     */
    public UserInfo getUserInfoByAccount(String account);

    /**
     *
     * @param roleId
     * @return
     */
    Set<String> getRoleMenuByRoleId(Long roleId);
}
