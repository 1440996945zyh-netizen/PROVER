package com.yy.ppm.master.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.master.bean.dto.MHqCargoDTO;
import com.yy.ppm.master.bean.dto.MHqStorageStackDTO;
import com.yy.ppm.master.bean.dto.MHqStorageStackSearchDTO;
import com.yy.ppm.master.mapper.MHqStorageStackMapper;
import com.yy.ppm.master.mapper.MMachineMapper;
import com.yy.ppm.master.service.MHqStorageStackService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class MHqStorageStackServiceImpl implements MHqStorageStackService {

    @Resource
    private MHqStorageStackMapper mHqStorageStackMapper;

    @Resource
    private Snowflake snowflake;

    @Override
    public Pages<MHqStorageStackDTO> getList(MHqStorageStackSearchDTO searchDTO) {
        Pages<MHqStorageStackDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mHqStorageStackMapper.getList(searchDTO);
        });
        return pages;
    }

    @Override
    public MHqStorageStackDTO getDetail(Long id) {
        return mHqStorageStackMapper.getById(id);
    }

    @Override
    public boolean doSave(MHqStorageStackDTO mHqStorageStackDTO) {
        // 新增
        if (mHqStorageStackDTO.getId() == null) {
            mHqStorageStackDTO.setId(snowflake.nextId());
            return mHqStorageStackMapper.insert(mHqStorageStackDTO) == 1;

            // 修改
        } else {
            return mHqStorageStackMapper.update(mHqStorageStackDTO) == 1;
        }
    }

    @Override
    public boolean deleteById(Long id) {
        return mHqStorageStackMapper.deleteById(id) == 1;
    }
}
