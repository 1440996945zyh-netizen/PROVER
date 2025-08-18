package com.yy.ppm.master.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.UserHelper;

import com.yy.ppm.master.service.MShipPrepayStdService;
import com.yy.ppm.master.mapper.MShipPrepayStdMapper;
import com.yy.ppm.master.bean.dto.MShipPrepayStdDTO;
import com.yy.ppm.master.bean.dto.MShipPrepayStdSearchDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 船舶预缴标准, (MShipPrepayStd)ServiceImpl
 * @Description
 * @createTime 2023年10月23日 15:50:00
 */
@Service
public class MShipPrepayStdServiceImpl implements MShipPrepayStdService {

    @Resource
    private MShipPrepayStdMapper mShipPrepayStdMapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<MShipPrepayStdDTO> getList(MShipPrepayStdSearchDTO searchDTO) {

        Pages<MShipPrepayStdDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mShipPrepayStdMapper.getList(searchDTO);
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
    public MShipPrepayStdDTO getDetail(Long id) {
        return mShipPrepayStdMapper.getById(id);
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(MShipPrepayStdDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return mShipPrepayStdMapper.insert(dto) == 1;

            // 修改
        } else {
            return mShipPrepayStdMapper.update(dto) == 1;
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

        return mShipPrepayStdMapper.deleteById(id) == 1;

    }
}

