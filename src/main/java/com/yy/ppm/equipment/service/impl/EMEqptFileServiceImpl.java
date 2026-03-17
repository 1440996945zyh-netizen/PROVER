package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.equipment.bean.dto.EMEqptFileDTO;
import com.yy.ppm.equipment.bean.dto.EMEqptFileSearchDTO;
import com.yy.ppm.equipment.bean.po.EMEqptFilePO;
import com.yy.ppm.equipment.mapper.EMEqptFileMapper;
import com.yy.ppm.equipment.service.EMEqptFileService;
import com.yy.ppm.common.service.SysFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * 设备资料文件Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class EMEqptFileServiceImpl implements EMEqptFileService {

    @Resource
    private EMEqptFileMapper mapper;

    @Resource
    private Snowflake snowflake;

    @Resource
    private SysFileService sysFileService;

    /**
     * 查询设备资料文件列表（分页）
     */
    @Override
    public Pages<EMEqptFileDTO> getList(EMEqptFileSearchDTO searchDTO) {
        return PageHelperUtils.limit(searchDTO, () -> mapper.selectList(searchDTO));
    }

    /**
     * 根据ID查询设备资料文件
     */
    @Override
    public EMEqptFileDTO getById(Long id) {
        return mapper.selectById(id);
    }

    /**
     * 新增或修改设备资料文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(EMEqptFileDTO dto) {
        EMEqptFilePO po = new EMEqptFilePO();
        BeanUtils.copyProperties(dto, po);

        if (dto.getId() == null) {
            // 新增
            po.setId(snowflake.nextId());
            mapper.insert(po);
            // 关联文件
            if (dto.getFileIds() != null && !dto.getFileIds().isEmpty()) {
                sysFileService.saveFileBusRelation(dto.getFileIds(), po.getId());
            }
        } else {
            // 修改
            mapper.update(po);
            // 关联文件
            if (dto.getFileIds() != null && !dto.getFileIds().isEmpty()) {
                sysFileService.saveFileBusRelation(dto.getFileIds(), po.getId());
            }
        }
    }

    /**
     * 删除设备资料文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }
}

