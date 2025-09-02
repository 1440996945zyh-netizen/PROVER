package com.yy.ppm.system.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.enums.CommonEnum;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.system.bean.dto.SysCustomRegionDTO;
import com.yy.ppm.system.mapper.SysCustomRegionMapper;
import com.yy.ppm.system.service.SysCustomRegionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName (SysCustomRegion)ServiceImpl
 * @author zws
 * @version 1.0.0
 * @Description
 * @createTime 2025年01月02日 11:14:00
 */
@Service
public class SysCustomRegionServiceImpl implements SysCustomRegionService {

    @Resource
    private SysCustomRegionMapper sysCustomRegionMapper;

    @Resource
    private Snowflake snowflake;

    @Resource
    private SecurityUtils securityUtils;

    @Resource
    private CommonMapper commonMapper;

    /**
     * 获取列表（翻页）
     *
     * @param
     * @return 对象列表
     */
    @Override
    public List<SysCustomRegionDTO> getList() {

        return sysCustomRegionMapper.getList(securityUtils.getLoginUserId());
    }

    /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     @Override
     public SysCustomRegionDTO getDetail(Long id) {
         return sysCustomRegionMapper.getById(id);
     }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(SysCustomRegionDTO dto) {

        dto.setUserAccount(securityUtils.getUserInfo().getUserAccount());
        dto.setUserId(securityUtils.getUserInfo().getId());

        // 删除单个
        sysCustomRegionMapper.deleteSingleData(dto);

        // 新增
        if (CommonEnum.YesNoMode.YES.getCode().equals(dto.getIsQuickEnter())) {
            dto.setId(snowflake.nextId());
            return sysCustomRegionMapper.insert(dto) == 1;
        }

        return true;

    }

    /**
     * 批量保存
     *
     * @param list
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doBatchInsert(List<SysCustomRegionDTO> list) {

        commonMapper.delete("SYS_CUSTOM_REGION", "USER_ID", securityUtils.getLoginUserId().toString());

        //验证有无重复
        Set<Long> menuIdSet = new HashSet<>();
        for (SysCustomRegionDTO dto : list) {
            if (menuIdSet.contains(dto.getMenuId())) {
                throw new BusinessRuntimeException("请选择不同的快捷入口！");
            }
            menuIdSet.add(dto.getMenuId());
        }

        for (SysCustomRegionDTO dto : list) {
            dto.setId(snowflake.nextId());
            dto.setUserAccount(securityUtils.getLoginUserAccount());
            dto.setUserId(securityUtils.getLoginUserId());
            dto.setCreateBy(securityUtils.getLoginUserId());
            dto.setCreateByName(securityUtils.getLoginUserName());
            dto.setCreateTime(new Date());
        }

        return sysCustomRegionMapper.batchInsert(list) > 0;

    }

    /**
     * 删除
     *
     * @param  id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {

        return sysCustomRegionMapper.deleteById(id) == 1;

    }
}

