package com.yy.ppm.business.service.impl;

import com.google.common.collect.Maps;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.UserHelper;

import com.yy.ppm.business.bean.dto.TBusOrderItemDTO;
import com.yy.ppm.business.service.TBusOrderService;
import com.yy.ppm.business.mapper.TBusOrderMapper;
import com.yy.ppm.business.bean.dto.TBusOrderDTO;
import com.yy.ppm.business.bean.dto.TBusOrderSearchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 委托单主表(TBusOrder)ServiceImpl
 * @Description
 * @createTime 2024年10月23日 09:01:00
 */
@Service
public class TBusOrderServiceImpl implements TBusOrderService {

    @Resource
    private TBusOrderMapper tBusOrderMapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TBusOrderDTO> getList(TBusOrderSearchDTO searchDTO) {
        Pages<TBusOrderDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tBusOrderMapper.getList(searchDTO);
        });
        return pages;
    }
    @Autowired
    private SecurityUtils securityUtils;

    @Override
    public boolean updateStatus(TBusOrderDTO tBusOrderDTO) {
        if(tBusOrderDTO.getStatus().equals(0L)){//待审核
            tBusOrderDTO.setApprovedBy(null);
            tBusOrderDTO.setApprovedByName(null);
            tBusOrderDTO.setApprovedTime(null);
        }
        if(tBusOrderDTO.getStatus().equals(1L)){//审核
            tBusOrderDTO.setApprovedBy(securityUtils.getLoginUserId());
            tBusOrderDTO.setApprovedByName(securityUtils.getLoginUserName());
            tBusOrderDTO.setApprovedTime(new Date());
        }
        return 1 == tBusOrderMapper.updateStatus(tBusOrderDTO);
    }



    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    @Override
    public Map<String,Object> getDetail(Long id) {
        TBusOrderDTO orderDTO = tBusOrderMapper.getById(id);
        List<Map<String,Object>> itemDTOList = tBusOrderMapper.getDetailByOrderId(id);
        Map<String,Object> result = Maps.newHashMap();
        result.put("orderDTO",orderDTO);
        result.put("itemDTO",itemDTOList);
        return result;
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TBusOrderDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return tBusOrderMapper.insert(dto) == 1;

            // 修改
        } else {
            return tBusOrderMapper.update(dto) == 1;
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

        return tBusOrderMapper.deleteById(id) == 1;

    }
}

