package com.yy.ppm.finance.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.finance.bean.dto.TFdBankAffiliatedDTO;
import com.yy.ppm.finance.bean.dto.TFdBankAffiliatedSearchDTO;
import com.yy.ppm.finance.mapper.TFdBankAffiliatedMapper;
import com.yy.ppm.finance.service.TFdBankAffiliatedService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 关联银行维护(TFdBankAffiliated)ServiceImpl
 * @Description
 * @createTime 2023年09月13日 15:16:00
 */
@Service
public class TFdBankAffiliatedServiceImpl implements TFdBankAffiliatedService {

    @Resource
    private TFdBankAffiliatedMapper tFdBankAffiliatedMapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TFdBankAffiliatedDTO> getList(TFdBankAffiliatedSearchDTO searchDTO) {

        Pages<TFdBankAffiliatedDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tFdBankAffiliatedMapper.getList(searchDTO);
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
    public TFdBankAffiliatedDTO getDetail(Long id) {
        return tFdBankAffiliatedMapper.getById(id);
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TFdBankAffiliatedDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return tFdBankAffiliatedMapper.insert(dto) == 1;

            // 修改
        } else {
            return tFdBankAffiliatedMapper.update(dto) == 1;
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

        return tFdBankAffiliatedMapper.deleteById(id) == 1;

    }
}

