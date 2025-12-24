package com.yy.ppm.system.service;


import com.yy.common.flowable.utils.CollectionUtils;
import com.yy.common.page.Pages;
import com.yy.ppm.system.bean.dto.ProfileDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.bean.dto.SysUserSearchDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    /**
     * 根据用户ids批量查询用户信息
     */
    public List<SysUserDTO> getUserList(Collection<Long> ids);

    /**
     * 获得用户 Map
     *
     * @param ids 用户编号数组
     * @return 用户 Map
     */
    default Map<Long, SysUserDTO> getUserMap(Collection<Long> ids) {
        List<SysUserDTO> users = getUserList(ids);
        return CollectionUtils.convertMap(users, SysUserDTO::getId);
    }


}
