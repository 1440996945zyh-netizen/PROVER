package com.yy.ppm.system.service;


import com.yy.ppm.system.bean.dto.SysCustomRegionDTO;
import com.yy.ppm.system.bean.dto.SysMenuDTO;

import java.util.List;

/**
 * @ClassName (SysCustomRegion)Service
 * @author zws
 * @version 1.0.0
 * @Description
 * @createTime 2025年01月02日 11:14:00
 */
public interface SysCustomRegionService {

    /**
     * 获取列表（翻页）
     *
     * @param
     * @return 对象列表
     */
    public List<SysCustomRegionDTO> getList();

    List<SysMenuDTO> getListApp();


     /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     public SysCustomRegionDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param sysCustomRegionDTO
     * @return 是否成功
     */
    public boolean doSave(SysCustomRegionDTO sysCustomRegionDTO);

    /**
     * 保存
     *
     * @param sysCustomRegionDTO
     * @return 是否成功
     */
    public boolean doAppSave(SysCustomRegionDTO sysCustomRegionDTO);


    boolean delRegion(SysCustomRegionDTO dto);

    /**
     * 批量保存
     *
     * @param list
     * @return 是否成功
     */
    public boolean doBatchInsert(List<SysCustomRegionDTO> list);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);


    /**
     * 保存
     * @param sysCustomRegionDTO
     * @return 是否成功
     */
    public boolean doAppSave(SysCustomRegionDTO sysCustomRegionDTO);

    /**
    * 删除App快捷菜单
     * @param dto
     * @return
     */
    boolean delAppRegion(SysCustomRegionDTO dto);

    /**
    * 获取App快捷菜单列表
     * @return
     */
    public  List<SysMenuDTO> getListApp();


}

