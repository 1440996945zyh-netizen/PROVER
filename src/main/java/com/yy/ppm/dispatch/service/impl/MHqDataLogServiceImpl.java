package com.yy.ppm.dispatch.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.UserHelper;

import com.yy.ppm.dispatch.service.MHqDataLogService;
import com.yy.ppm.dispatch.mapper.MHqDataLogMapper;
import com.yy.ppm.dispatch.bean.dto.MHqDataLogDTO;
import com.yy.ppm.dispatch.bean.dto.MHqDataLogSearchDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 海清货物变更日志表(MHqDataLog)ServiceImpl
 * @Description
 * @createTime 2025年05月27日 18:20:00
 */
@Service
public class MHqDataLogServiceImpl implements MHqDataLogService {

    @Resource
    private MHqDataLogMapper mHqDataLogMapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<MHqDataLogDTO> getList(MHqDataLogSearchDTO searchDTO) {

        Pages<MHqDataLogDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mHqDataLogMapper.getList(searchDTO);
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
    public MHqDataLogDTO getDetail(Long id) {
        return mHqDataLogMapper.getById(id);
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(MHqDataLogDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return mHqDataLogMapper.insert(dto) == 1;

            // 修改
        } else {
            return mHqDataLogMapper.update(dto) == 1;
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

        return mHqDataLogMapper.deleteById(id) == 1;

    }
}

