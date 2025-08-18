package com.yy.ppm.system.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.system.bean.dto.SysRoleDTO;
import com.yy.ppm.system.bean.dto.SysRoleSearchDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.bean.dto.TreeSelectDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色(SysRole)Dao
 *
 * @author 张超
 * @date 2021-03-02 09:34:53
 */
public interface SysRoleMapper {

    /**
     * 获取角色列表
     *
     * @param sysRoleSearchDTO 角色SearchDTO
     * @return
     */
    Page<SysRoleDTO> getList(SysRoleSearchDTO sysRoleSearchDTO);
    List<SysRoleDTO> getListNoPage(SysRoleSearchDTO sysRoleSearchDTO);

    List<SysRoleDTO> getListByRoleClass(String sysRoleSearchDTO);

    /**
     * 根据gid获取角色
     *
     * @param id 主键
     * @return
     */
    SysRoleDTO getById(Long id);

    /**
     * 新增角色
     *
     * @param sysRoleDTO 角色DTO
     * @return
     */
    @Edit
    int insert(SysRoleDTO sysRoleDTO);

    /**
     * 修改角色
     *
     * @param sysRoleDTO 角色DTO
     * @return
     */
    @Edit
    int update(SysRoleDTO sysRoleDTO);


    /**
     * 批量新增角色菜单信息
     * @param menuIds
     * @param id
     */
    int insertRoleMenu(@Param("menuIds") List<Long> menuIds, @Param("id") Long id);

    List<Long> getMenuIdByRole(Long id);

    int changeStatus(SysRoleDTO sysRoleDTO);

    List<TreeSelectDTO> getDept(Long parentId);

    List<Long> getDeptIdByRole(Long id);

    int insertRoleDept(@Param("roleId") Long roleId, @Param("deptIds") List<Long> deptIds);

    List<SysUserDTO> allocatedOrUnallocatedList(@Param("roleId") Long roleId, @Param("flag") String flag, @Param("nameOrAccount") String nameOrAccount);

    int authUserCancel(@Param("userIds") List<Long> userIds, @Param("roleId") Long roleId);

    int insertRoleUser(@Param("userIds") List<Long> userIds, @Param("roleId") Long roleId);
}

