package com.yy.ppm.system.service;

import com.yy.ppm.system.bean.dto.SysMenuDTO;
import com.yy.ppm.system.bean.dto.SysMenuSearchDTO;
import com.yy.ppm.system.bean.dto.TreeSelectDTO;

import java.util.List;

/**
 * 菜单(SysMenu)表服务接口
 *
 * @author 张超
 * @date 2021-02-26 15:40:58
 */
public interface SysMenuService {

    /**
     * 保存菜单
     *
     * @param sysMenuDTO
     * @return
     */
    Long save(SysMenuDTO sysMenuDTO);

    /**
     * 删除
     * @param id
     * @return
     */
    int deleteById(Long id);

    /**
     * 根据parentid获取菜单
     * @param parentGid 父gid
     * @return
     */
    List<SysMenuDTO> getByParentGid(long parentGid);


    /**
     * 查询全部菜单
     * @param menu
     * @return
     */
    List<SysMenuDTO> selectMenuList(SysMenuSearchDTO menu);
    List<SysMenuDTO> listApp(SysMenuSearchDTO menu);
    List<SysMenuDTO> listApplet(SysMenuSearchDTO menu);


    /**
     * 查询单条菜单详情
     * @param id
     * @return
     */
    SysMenuDTO getDetailById(Long id);

    List<TreeSelectDTO> getTreeSelect();



}
