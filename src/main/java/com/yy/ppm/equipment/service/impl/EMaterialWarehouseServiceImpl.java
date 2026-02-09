package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseSearchDTO;
import com.yy.ppm.equipment.bean.po.EMaterialWarehousePO;
import com.yy.ppm.equipment.mapper.EMaterialWarehouseMapper;
import com.yy.ppm.equipment.service.EMaterialWarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * 物资仓库Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class EMaterialWarehouseServiceImpl implements EMaterialWarehouseService {

    @Resource
    private EMaterialWarehouseMapper mapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 查询物资仓库列表（分页）
     */
    @Override
    public Pages<EMaterialWarehouseDTO> getList(EMaterialWarehouseSearchDTO searchDTO) {
        return PageHelperUtils.limit(searchDTO, () -> mapper.selectList(searchDTO));
    }

    /**
     * 根据ID查询物资仓库
     */
    @Override
    public EMaterialWarehouseDTO getById(Long id) {
        return mapper.selectById(id);
    }

    /**
     * 新增或修改物资仓库
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(EMaterialWarehouseDTO dto) {
        // 验证必填字段
        if (dto.getWarehouseCode() == null || dto.getWarehouseCode().trim().isEmpty()) {
            throw new BusinessRuntimeException("仓库编号不能为空");
        }
        if (dto.getWarehouseName() == null || dto.getWarehouseName().trim().isEmpty()) {
            throw new BusinessRuntimeException("仓库名称不能为空");
        }
        if (dto.getCompanyId() == null) {
            throw new BusinessRuntimeException("公司名称不能为空");
        }

        // 验证仓库编号是否重复
        int count = mapper.countByWarehouseCode(dto.getWarehouseCode(), dto.getId());
        if (count > 0) {
            throw new BusinessRuntimeException("仓库编号已存在");
        }

        EMaterialWarehousePO po = new EMaterialWarehousePO();
        BeanUtils.copyProperties(dto, po);

        if (dto.getId() == null) {
            // 新增
            po.setId(snowflake.nextId());
            mapper.insert(po);
        } else {
            // 修改
            mapper.update(po);
        }
    }

    /**
     * 删除物资仓库
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteById(Long id) {
        EMaterialWarehousePO po = new EMaterialWarehousePO();
        po.setId(id);
        mapper.deleteById(po);
    }

    /**
     * 查询物资仓库列表（不分页，用于下拉框）
     */
    @Override
    public java.util.List<EMaterialWarehouseDTO> getListForSelect() {
        return mapper.selectListForSelect();
    }
}

