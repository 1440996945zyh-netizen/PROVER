package com.yy.ppm.system.service;


import com.yy.common.page.Pages;
import com.yy.ppm.system.bean.dto.ProfileDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.bean.dto.SysUserSearchDTO;

import java.util.List;

/**
 * 用户表 系统用户表(UserModule)表服务接口
 * @author 张超
 * @date 2021-01-25
 */
public interface SysUserService {

    /**
     * 获取数据列表
     * @param sysUserSearchDTO
     * @return
     */
    public Pages<SysUserDTO> getList(SysUserSearchDTO sysUserSearchDTO);

    /**
     * 根据用户ID获取用户信息
     * @param id 主键
     * @return
     */
    public SysUserDTO getById(Long id);

    /**
     * 内部管理员创建内部用户，公司管理员创建用户时，往各自库创建数据。
     * @param sysUserDTO
     * @return
     */
    public int save(SysUserDTO sysUserDTO);

    /**
     * 删除
     * @param idList
     * @return
     */
    public int deleteById(List<Long> idList);

    /**
     * 重置密码
     * @param id
     * @return
     */
    public int resetpassword(Long id);

    /**
     * 修改手机号
     * @param sysUserDTO
     * @return
     */
    public int updatePhone(SysUserDTO sysUserDTO);

    /**
     * 修改邮箱
     * @param sysUserDTO
     * @return
     */
    public int updateEmail(SysUserDTO sysUserDTO);

    /**
     * 修改状态
     * @param sysUserDTO
     * @return
     */
    public int updateStatus(SysUserDTO sysUserDTO);

    /**
     * 修改密码
     * @param sysUserDTO
     * @return
     */
    public int updatePassword(SysUserDTO sysUserDTO);

    public ProfileDTO profile();

}
