package com.yy.ppm.produce.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.UserHelper;

import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.produce.bean.dto.TWeightPlanDTO;
import com.yy.ppm.produce.mapper.TWeightPlanMapper;
import com.yy.ppm.produce.service.TWeightPlanItemService;
import com.yy.ppm.produce.mapper.TWeightPlanItemMapper;
import com.yy.ppm.produce.bean.dto.TWeightPlanItemDTO;
import com.yy.ppm.produce.bean.dto.TWeightPlanItemSearchDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName (TWeightPlanItem)ServiceImpl
 * @Description
 * @createTime 2023年12月05日 08:39:00
 */
@Service
public class TWeightPlanItemServiceImpl implements TWeightPlanItemService {

    @Resource
    private TWeightPlanItemMapper tWeightPlanItemMapper;

    @Resource
    private TWeightPlanMapper tWeightPlanMapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TWeightPlanItemDTO> getList(TWeightPlanItemSearchDTO searchDTO) {

        Pages<TWeightPlanItemDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tWeightPlanItemMapper.getList(searchDTO);
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
    public TWeightPlanItemDTO getDetail(Long id) {
        return tWeightPlanItemMapper.getById(id);
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TWeightPlanItemDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return tWeightPlanItemMapper.insert(dto) == 1;

            // 修改
        } else {
            return tWeightPlanItemMapper.update(dto) == 1;
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

        return tWeightPlanItemMapper.deleteById(id) == 1;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeChildStatus(TWeightPlanItemDTO tWeightPlanItemDTO) {
        if(tWeightPlanItemDTO.getStatus() == 0L) {
            List<TWeightPlanDTO> list1 = tWeightPlanMapper.getListStatus();
            list1.forEach(x->{
                List<TWeightPlanItemDTO> carList = tWeightPlanItemMapper.getByParentId(x.getId());
                for(TWeightPlanItemDTO tWeightPlanItemDTO1 :carList){
                    if(tWeightPlanItemDTO1.getTruckNo().equals(tWeightPlanItemDTO.getTruckNo()) && tWeightPlanItemDTO1.getStatus() == 0L){
                        throw new BusinessRuntimeException(tWeightPlanItemDTO.getTruckNo()+"已存在其他杂货计划中");
                    }
                    if(tWeightPlanItemDTO1.getIdNumber().equals(tWeightPlanItemDTO.getIdNumber()) && tWeightPlanItemDTO1.getStatus() == 0L){
                        throw new BusinessRuntimeException(tWeightPlanItemDTO.getIdNumber()+"已存在其他杂货计划中");
                    }
                }
            });
        }
        if(tWeightPlanItemDTO.getId() == null){
            throw new BusinessRuntimeException("");
        }
        return tWeightPlanItemMapper.changeChildStatus(tWeightPlanItemDTO);
    }
}

