package com.yy.ppm.system.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.system.bean.dto.SysRoleDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.bean.dto.SysUserSearchDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 用户服务支撑Dao
 * @author 张超
 * @date 2021-02-25
 */
public interface SysUserMapper {

	/**
	 * 获取用户列表
	 * @param sysUserSearchDTO 用户查询DTO
	 * @return
	 */
	public Page<SysUserDTO> getList(SysUserSearchDTO sysUserSearchDTO);

	/**
	 * 根据gid获取用户信息
	 * @param id 主键
	 * @return
	 */
	public SysUserDTO getById(Long id);

	/**
	 * 新增用户信息
	 * @param sysUserDTO 用户DTO
	 * @return
	 */
	@Edit
	public int insert(SysUserDTO sysUserDTO);

	/**
	 * 修改用户信息
	 * @param sysUserDTO 用户DTO
	 * @return
	 */
	@Edit
	public int update(SysUserDTO sysUserDTO);

	/**
	 * 密码修改
	 * @param sysUserDTO
	 * @return
	 */
	@Edit
	public int updatePassword(SysUserDTO sysUserDTO);

	/**
	 * 修改手机号, 邮箱
	 * @param sysUserDTO
	 * @return
	 */
	@Edit
	public int updatePkInfo(SysUserDTO sysUserDTO);

	/**
	 * 批量新增用户角色信息
	 * @param roleId
	 * @param userId
	 */
    int insertRoleUser(Long roleId , Long userId);

	/**
	 * 查询用户角色
	 * @param id
	 * @return
	 */
	List<Long> getRoleListByUserId(Long id);

	/**
	 * 根据用户ids批量查询用户信息
	 */
    List<SysUserDTO> getUserList(@Param("list") Collection<Long> ids);


}
