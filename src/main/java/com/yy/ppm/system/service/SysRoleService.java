package com.yy.ppm.system.service;

import com.yy.common.page.Pages;
import com.yy.ppm.system.bean.dto.SysRoleDTO;
import com.yy.ppm.system.bean.dto.SysRoleSearchDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 角色(SysRole)表服务接口
 *
 * @author 张超
 * @date 2021-03-02 09:35:29
 */
public interface SysRoleService {

    /**
     * 获取数据列表
     *
     * @param sysRoleSearchDTO
     * @return
     */
    Pages<SysRoleDTO> getList(SysRoleSearchDTO sysRoleSearchDTO);

    List<SysRoleDTO> getListNoPage(SysRoleSearchDTO sysRoleSearchDTO);

    /**
     * 根据角色GID获取角色
     *
     * @param id 主键
     * @return
     */
    SysRoleDTO getById(Long id);

    /**
     * 保存角色
     *
     * @param sysRoleDTO
     * @return
     */
    int save(SysRoleDTO sysRoleDTO);

    /**
     * 删除角色
     * @param idList
     * @return
     */
    int deleteById(List<Long> idList);

    int changeStatus(SysRoleDTO sysRoleDTO);

    Map getDeptTree(Long id);

    int dataScope(SysRoleDTO sysRoleDTO);

    /**
     * 查询已授权和未授权用户列表
     * @param roleId
     * @param nameOrAccount
     * @return
     */
    List<SysUserDTO> allocatedOrUnallocatedList(Long roleId, String nameOrAccount, String flag);

    /**
     * 授权或取消授权
     * @param paramMap
     * @return
     */
    int authUserInsertDelete(Map paramMap);

    /**
     * 根据角色获取有权限菜单和菜单树
     * @param id
     * @return
     */
    Map getMenuTree(Long id);

    /**
     * 获取
     * @param sysRoleSearchDTO
     * @return
     */
    List<Map<String,Object>> getListByRoleClass(String sysRoleSearchDTO);

    /**
     * 校验角色们是否有效。如下情况，视为无效：
     * 1. 角色编号不存在
     * 2. 角色被禁用
     *
     * @param roleIds 角色编号数组
     */
    void validRoleList(Set<Long> roleIds);

    /**
     * 获得拥有多个角色的用户编号集合
     * @param roleIds
     * @return
     */
    Set<Long> getUserRoleIdListByRoleIds(Set<Long> roleIds);
}
