package com.yy.ppm.master.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.UserHelper;

import com.yy.ppm.master.service.MCityService;
import com.yy.ppm.master.mapper.MCityMapper;
import com.yy.ppm.master.bean.dto.MCityDTO;
import com.yy.ppm.master.bean.dto.MCitySearchDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;

 /**
 * @ClassName (MCity)ServiceImpl
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月30日 13:29:00
 */
@Service
public class MCityServiceImpl implements MCityService {

    @Resource
    private MCityMapper mCityMapper;

    @Resource
	private Snowflake snowflake;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<MCityDTO> getList(MCitySearchDTO searchDTO) {

    	Pages<MCityDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mCityMapper.getList(searchDTO);
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
     public MCityDTO getDetail(Long id) {
         return mCityMapper.getById(id);
     }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(MCityDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return mCityMapper.insert(dto) == 1;

            // 修改
        } else {
            return mCityMapper.update(dto) == 1;
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

        return mCityMapper.deleteById(id) == 1;

    }
}

