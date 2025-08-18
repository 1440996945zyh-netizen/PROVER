package com.yy.ppm.finance.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;

import com.yy.ppm.finance.service.TFdDebtorpaymentDetailService;
import com.yy.ppm.finance.mapper.TFdDebtorpaymentDetailMapper;
import com.yy.ppm.finance.bean.dto.TFdDebtorpaymentDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdDebtorpaymenDetailSearchDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 收据主表(TFdDebtorpaymenDetail)ServiceImpl
 * @Description
 * @createTime 2023年09月20日 11:44:00
 */
@Service
public class TFdDebtorpaymentDetailServiceImpl implements TFdDebtorpaymentDetailService {

    @Resource
    private TFdDebtorpaymentDetailMapper tFdDebtorpaymentDetailMapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TFdDebtorpaymentDetailDTO> getList(TFdDebtorpaymenDetailSearchDTO searchDTO) {

        Pages<TFdDebtorpaymentDetailDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tFdDebtorpaymentDetailMapper.getList(searchDTO);
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
    public TFdDebtorpaymentDetailDTO getDetail(Long id) {
        return tFdDebtorpaymentDetailMapper.getById(id);
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TFdDebtorpaymentDetailDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return tFdDebtorpaymentDetailMapper.insert(dto) == 1;

            // 修改
        } else {
            return tFdDebtorpaymentDetailMapper.update(dto) == 1;
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

        return tFdDebtorpaymentDetailMapper.deleteById(id) == 1;

    }
}

