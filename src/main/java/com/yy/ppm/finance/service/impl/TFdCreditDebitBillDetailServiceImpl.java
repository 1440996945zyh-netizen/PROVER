package com.yy.ppm.finance.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.UserHelper;

import com.yy.ppm.finance.service.TFdCreditDebitBillDetailService;
import com.yy.ppm.finance.mapper.TFdCreditDebitBillDetailMapper;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillDetailSearchDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 贷方解放票据主表(TFdCreditDebitBillDetail)ServiceImpl
 * @Description
 * @createTime 2023年10月08日 16:19:00
 */
@Service
public class TFdCreditDebitBillDetailServiceImpl implements TFdCreditDebitBillDetailService {

    @Resource
    private TFdCreditDebitBillDetailMapper tFdCreditDebitBillDetailMapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TFdCreditDebitBillDetailDTO> getList(TFdCreditDebitBillDetailSearchDTO searchDTO) {

        Pages<TFdCreditDebitBillDetailDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tFdCreditDebitBillDetailMapper.getList(searchDTO);
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
    public TFdCreditDebitBillDetailDTO getDetail(Long id) {
        return tFdCreditDebitBillDetailMapper.getById(id);
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TFdCreditDebitBillDetailDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return tFdCreditDebitBillDetailMapper.insert(dto) == 1;

            // 修改
        } else {
            return tFdCreditDebitBillDetailMapper.update(dto) == 1;
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

        return tFdCreditDebitBillDetailMapper.deleteById(id) == 1;

    }
}

