package com.yy.ppm.master.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.master.bean.dto.MHqCargoDTO;
import com.yy.ppm.master.bean.dto.MHqCargoSearchDTO;
import com.yy.ppm.master.mapper.MHqCargoMapper;
import com.yy.ppm.master.service.MHqCargoService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class MHqCargoServiceImpl implements MHqCargoService {

    @Resource
    private MHqCargoMapper mHqCargoMapper;

    @Resource
    private Snowflake snowflake;

    @Override
    public Pages<MHqCargoDTO> getList(MHqCargoSearchDTO searchDTO) {
        Pages<MHqCargoDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mHqCargoMapper.getList(searchDTO);
        });

        return pages;
    }

    @Override
    public MHqCargoDTO getDetail(Long id) {
        return mHqCargoMapper.getById(id);
    }

    @Override
    public boolean doSave(MHqCargoDTO mHqCargoDTO) {
        // 新增
        if (mHqCargoDTO.getId() == null) {
            mHqCargoDTO.setId(snowflake.nextId());
            return mHqCargoMapper.insert(mHqCargoDTO) == 1;

            // 修改
        } else {
            return mHqCargoMapper.update(mHqCargoDTO) == 1;
        }
    }

    @Override
    public boolean deleteById(Long id) {
        return mHqCargoMapper.deleteById(id) == 1;
    }
}
