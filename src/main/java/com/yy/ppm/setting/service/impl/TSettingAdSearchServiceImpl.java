package com.yy.ppm.setting.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;

import com.yy.ppm.setting.bean.dto.TSettingAdSearchDTO;
import com.yy.ppm.setting.bean.dto.TSettingAdSearchSearchDTO;
import com.yy.ppm.setting.mapper.TSettingAdSearchMapper;
import com.yy.ppm.setting.service.TSettingAdSearchService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;

import java.util.List;


/**
 * @ClassName 高級查詢配置表(TSettingAdSearch)ServiceImpl
 * @author zws
 * @version 1.0.0
 * @Description
 * @createTime 2025年09月25日 15:35:00
 */
@Service
public class TSettingAdSearchServiceImpl implements TSettingAdSearchService {

    @Resource
    private TSettingAdSearchMapper tSettingAdSearchMapper;

     /**
      * 雪花算法
      **/
     @Autowired
     private Snowflake snowflake;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TSettingAdSearchDTO> getList(TSettingAdSearchSearchDTO searchDTO) {

    	Pages<TSettingAdSearchDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tSettingAdSearchMapper.getList(searchDTO);
		});

        return pages;
    }

    /**
      * 查询单条记录
      *
      * @param menuId,tableId
      * @return 实体
      */
     @Override
     public List<TSettingAdSearchDTO> getDetail(Long menuId, String tableId) {
         return tSettingAdSearchMapper.getByMenuIdAndTableId(menuId, tableId);
     }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TSettingAdSearchDTO dto) {
        int count = 1;
        // 新增(此处无法验重，无法控制menuId和tableId的重复)
        //先删后插
        tSettingAdSearchMapper.deleteByMenuIdAndTableId(dto.getMenuId(), dto.getTableId());
        for (TSettingAdSearchDTO tSettingAdSearchDTO : dto.getSettingAdSearchList()) {
            tSettingAdSearchDTO.setId(snowflake.nextId());
            tSettingAdSearchDTO.setMenuId(dto.getMenuId());
            tSettingAdSearchDTO.setTableId(dto.getTableId());
            tSettingAdSearchDTO.setSortNum(count++);
        }
        tSettingAdSearchMapper.insertBatch(dto.getSettingAdSearchList());

        return count > 1 ;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long menuId, String tableId) {
        if (menuId == null || tableId == null) {
            throw new IllegalArgumentException("menuId和tableId不能为空");
        }

        int count = tSettingAdSearchMapper.deleteByMenuIdAndTableId(menuId, tableId);
        return count > 0;
    }



}

