package com.yy.ppm.produce.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.UserHelper;

import com.yy.ppm.produce.service.TPrdDySumService;
import com.yy.ppm.produce.mapper.TPrdDySumMapper;
import com.yy.ppm.produce.bean.dto.TPrdDySumDTO;
import com.yy.ppm.produce.bean.dto.TPrdDySumSearchDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (TPrdDySum)ServiceImpl
 * @Description
 * @createTime 2024年12月03日 17:07:00
 */
@Service
public class TPrdDySumServiceImpl implements TPrdDySumService {

    @Resource
    private TPrdDySumMapper tPrdDySumMapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TPrdDySumDTO> getList(TPrdDySumSearchDTO searchDTO) {

        Pages<TPrdDySumDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tPrdDySumMapper.getList(searchDTO);
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
    public TPrdDySumDTO getDetail(Long id) {
        return tPrdDySumMapper.getById(id);
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TPrdDySumDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return tPrdDySumMapper.insert(dto) == 1;

            // 修改
        } else {
            return tPrdDySumMapper.update(dto) == 1;
        }

    }

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {

        return tPrdDySumMapper.deleteById(id) == 1;

    }
}

