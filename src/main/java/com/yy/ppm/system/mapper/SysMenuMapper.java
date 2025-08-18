package com.yy.ppm.system.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.system.bean.dto.SysMenuDTO;
import com.yy.ppm.system.bean.dto.SysMenuSearchDTO;
import com.yy.ppm.system.bean.dto.TreeSelectDTO;

import java.util.List;

/**
 * 菜单(SysMenu)Dao
 *
 * @author 张超
 * @date 2021-02-26 15:40:00
 */
public interface SysMenuMapper {

    /**
     * 根据gid获取菜单
     *
     * @param gid 主键
     * @return
     */
    SysMenuDTO getDetailById(Long gid);

    /**
     * 新增菜单
     *
     * @param sysMenuDTO 菜单DTO
     * @return
     */
    @Edit
    int insert(SysMenuDTO sysMenuDTO);

    /**
     * 修改菜单
     *
     * @param sysMenuDTO 菜单DTO
     * @return
     */
    @Edit
    int update(SysMenuDTO sysMenuDTO);

    /**
     * 根据父gid获取菜单信息
     * @param parentId
     * @return
     */
    List<SysMenuDTO> getByParentId(SysMenuSearchDTO menu);


    /**
     * 获取全部菜单
     * @param menu
     * @return
     */
    List<SysMenuDTO> selectMenuList(SysMenuSearchDTO menu);
    List<SysMenuDTO> listApp(SysMenuSearchDTO menu);

    List<SysMenuDTO> listApplet(SysMenuSearchDTO menu);

    List<TreeSelectDTO> getTreeSelect(Long parentId);
}

