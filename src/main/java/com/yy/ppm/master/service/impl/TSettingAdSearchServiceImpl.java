package com.yy.ppm.master.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.UserHelper;

import com.yy.ppm.master.service.TSettingAdSearchService;
import com.yy.ppm.master.mapper.TSettingAdSearchMapper;
import com.yy.ppm.master.bean.dto.TSettingAdSearchDTO;
import com.yy.ppm.master.bean.dto.TSettingAdSearchSearchDTO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;


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
      * @param id
      * @return 实体
      */
     @Override
     public TSettingAdSearchDTO getDetail(Long id) {
         return tSettingAdSearchMapper.getById(id);
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

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return tSettingAdSearchMapper.insert(dto) == 1;

            // 修改
        } else {
            return tSettingAdSearchMapper.update(dto) == 1;
        }

    }

    /**
     * 删除
     *
     * @param  id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {

        return tSettingAdSearchMapper.deleteById(id) == 1;

    }
}

