package com.yy.ppm.finance.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.github.pagehelper.Page;
import com.google.api.client.util.Lists;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.finance.bean.dto.TFdBankPayDTO;
import com.yy.ppm.finance.bean.dto.TFdBankPaySearchDTO;
import com.yy.ppm.finance.mapper.TFdBankPayMapper;
import com.yy.ppm.finance.service.TFdBankPayService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rzg
 * @version 1.0.0
 * @ClassName 付款银行维护(TFdBankPay)ServiceImpl
 * @Description
 * @createTime 2023年09月13日 16:23:00
 */
@Service
public class TFdBankPayServiceImpl implements TFdBankPayService {

    @Resource
    private TFdBankPayMapper tFdBankPayMapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TFdBankPayDTO> getList(TFdBankPaySearchDTO searchDTO) {

        Pages<TFdBankPayDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tFdBankPayMapper.getList(searchDTO);
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
    public TFdBankPayDTO getDetail(Long id) {
        return tFdBankPayMapper.getById(id);
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TFdBankPayDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return tFdBankPayMapper.insert(dto) == 1;

            // 修改
        } else {
            return tFdBankPayMapper.update(dto) == 1;
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

        return tFdBankPayMapper.deleteById(id) == 1;

    }


    /**
     * 获取银行下拉框
     * @return
     */
    @Override
    public List<Map<String, Object>> getSelectList() {

        List<Map<String, Object>> result = Lists.newArrayList();

        Page<TFdBankPayDTO> list = tFdBankPayMapper.getList(new TFdBankPaySearchDTO());
        if(CollectionUtils.isEmpty(list)){
            return result;
        }
        list.forEach(val->{
            HashMap<String, Object> objectObjectHashMap = new HashMap<>();
            objectObjectHashMap.put("label",val.getBankName());
            objectObjectHashMap.put("value",val.getId());
            result.add(objectObjectHashMap);
        });

        return result;
    }
}

