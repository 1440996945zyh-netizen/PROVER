package com.yy.ppm.system.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;

import com.yy.ppm.system.service.SysLoginLogService;
import com.yy.ppm.system.mapper.SysLoginLogMapper;
import com.yy.ppm.system.bean.dto.SysLoginLogDTO;
import com.yy.ppm.system.bean.dto.SysLoginLogSearchDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;

 /**
 * @ClassName 登录日志表(SysLoginLog)ServiceImpl
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 15:51:00
 */
@Service
public class SysLoginLogServiceImpl implements SysLoginLogService {

    @Resource
    private SysLoginLogMapper sysLoginLogMapper;

    @Resource
	private Snowflake snowflake;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<SysLoginLogDTO> getList(SysLoginLogSearchDTO searchDTO) {

    	Pages<SysLoginLogDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return sysLoginLogMapper.getList(searchDTO);
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
     public SysLoginLogDTO getDetail(Long id) {
         return sysLoginLogMapper.getById(id);
     }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(SysLoginLogDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return sysLoginLogMapper.insert(dto) == 1;

            // 修改
        } else {
            return sysLoginLogMapper.update(dto) == 1;
        }

    }

    /**
     * 删除
     *
     * @param  id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {

        return sysLoginLogMapper.deleteById(id) == 1;

    }
}

