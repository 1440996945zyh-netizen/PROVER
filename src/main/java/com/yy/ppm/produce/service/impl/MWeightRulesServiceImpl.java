package com.yy.ppm.produce.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.UserHelper;

import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.produce.service.MWeightRulesService;
import com.yy.ppm.produce.mapper.MWeightRulesMapper;
import com.yy.ppm.produce.bean.dto.MWeightRulesDTO;
import com.yy.ppm.produce.bean.dto.MWeightRulesSearchDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName (MWeightRules)ServiceImpl
 * @Description
 * @createTime 2023年11月30日 17:20:00
 */
@Service
public class MWeightRulesServiceImpl implements MWeightRulesService {

    @Resource
    private MWeightRulesMapper mWeightRulesMapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<MWeightRulesDTO> getList(MWeightRulesSearchDTO searchDTO) {

        Pages<MWeightRulesDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mWeightRulesMapper.getList(searchDTO);
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
    public MWeightRulesDTO getDetail(Long id) {
        return mWeightRulesMapper.getById(id);
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(MWeightRulesDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            List<MWeightRulesDTO> allList = mWeightRulesMapper.getAllList();
            allList.forEach(x->{
                if (x != null && x.getCargoCode() != null && x.getCargoCode().equals(dto.getCargoCode())) {
                    throw new BusinessRuntimeException("货物已存在");
                }
            });
            return mWeightRulesMapper.insert(dto) == 1;

            // 修改
        } else {
            return mWeightRulesMapper.update(dto) == 1;
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

        return mWeightRulesMapper.deleteById(id) == 1;

    }
}

