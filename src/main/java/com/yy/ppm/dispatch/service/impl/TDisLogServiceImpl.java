package com.yy.ppm.dispatch.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.UserHelper;

import com.yy.ppm.common.mapper.SysFileMapper;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.dispatch.service.TDisLogService;
import com.yy.ppm.dispatch.mapper.TDisLogMapper;
import com.yy.ppm.dispatch.bean.dto.TDisLogDTO;
import com.yy.ppm.dispatch.bean.dto.TDisLogSearchDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName 调度日志(TDisLog)ServiceImpl
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:45:00
 */
@Service
public class TDisLogServiceImpl implements TDisLogService {

    @Resource
    private TDisLogMapper tDisLogMapper;

    @Resource
	private Snowflake snowflake;

    @Resource
    private SysFileService sysFileService;
    @Resource
    private SecurityUtils securityUtils;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TDisLogDTO> getList(TDisLogSearchDTO searchDTO) {

    	Pages<TDisLogDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tDisLogMapper.getList(searchDTO);
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
     public TDisLogDTO getDetail(Long id) {
         return tDisLogMapper.getById(id);
     }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TDisLogDTO dto) {

        int count = 0;
        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            count =  tDisLogMapper.insert(dto) ;

            // 修改
        } else {
            dto.setUpdateBy(securityUtils.getLoginUserId());
            dto.setUpdateByName(securityUtils.getLoginUserName());
            dto.setUpdateTime(new Date());
            count =  tDisLogMapper.update(dto) ;
        }

        // 附件保存
        sysFileService.saveFileBusRelation(dto.getFileIds(), dto.getId());
        return count > 0;

    }

    /**
     * 删除
     *
     * @param  id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {

        return tDisLogMapper.deleteById(id) == 1;

    }
}

