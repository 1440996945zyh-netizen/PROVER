package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.EMaterialCategoryDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialCodeDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialCodeSearchDTO;
import com.yy.ppm.equipment.bean.po.EMaterialCodePO;
import com.yy.ppm.equipment.mapper.EMaterialCodeMapper;
import com.yy.ppm.equipment.service.EMaterialCodeService;
import com.yy.ppm.equipment.service.EMaterialCategoryService;
import com.yy.ppm.common.service.SysFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 物资代码Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class EMaterialCodeServiceImpl implements EMaterialCodeService {

    @Resource
    private EMaterialCodeMapper mapper;

    @Resource
    private Snowflake snowflake;

    @Resource
    private EMaterialCategoryService categoryService;

    @Resource
    private SysFileService sysFileService;

    /**
     * 查询物资代码列表（分页）
     */
    @Override
    public Pages<EMaterialCodeDTO> getList(EMaterialCodeSearchDTO searchDTO) {
        Pages<EMaterialCodeDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mapper.selectList(searchDTO);
        });
        return pages;
    }

    /**
     * 根据ID查询物资代码
     */
    @Override
    public EMaterialCodeDTO getById(Long id) {
        return mapper.selectById(id);
    }

    /**
     * 新增或修改物资代码
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(EMaterialCodeDTO dto) {
        // 验证必填字段
        if (dto.getCategoryId() == null) {
            throw new BusinessRuntimeException("类别ID不能为空");
        }
        if (dto.getMaterialName() == null || dto.getMaterialName().trim().isEmpty()) {
            throw new BusinessRuntimeException("物资名称不能为空");
        }
        if (dto.getPurchaseTypeCode() == null || dto.getPurchaseTypeCode().trim().isEmpty()) {
            throw new BusinessRuntimeException("采购类型不能为空");
        }
        if (dto.getUnitCode() == null || dto.getUnitCode().trim().isEmpty()) {
            throw new BusinessRuntimeException("计量单位不能为空");
        }

        // 验证类别必须是三级分类
        EMaterialCategoryDTO category = categoryService.getById(dto.getCategoryId());
        if (category == null) {
            throw new BusinessRuntimeException("物资类别不存在");
        }
        if (category.getCategoryLevel() == null || category.getCategoryLevel() != 3) {
            throw new BusinessRuntimeException("物资代码只能选择三级分类");
        }

        EMaterialCodePO po = new EMaterialCodePO();
        BeanUtils.copyProperties(dto, po);

        if (dto.getId() == null) {
            // 新增
            po.setId(snowflake.nextId());
            
            // 自动生成物资代码
            // 获取三级分类的codeCount
            Integer categoryCodeCount = category.getCodeCount();
            if (categoryCodeCount == null) {
                categoryCodeCount = 0;
            }
            
            // 加1
            Integer newCodeCount = categoryCodeCount + 1;
            
            // 补成3位（001格式）
            String formattedCodeCount = String.format("%03d", newCodeCount);
            
            // 获取三级分类的编码
            String categoryCode = category.getCategoryCode();
            if (categoryCode == null || categoryCode.trim().isEmpty()) {
                throw new BusinessRuntimeException("三级分类编码为空，无法生成物资代码");
            }
            
            // 生成物资代码：三级分类编码 + '-' + formattedCodeCount
            String generatedMaterialCode = categoryCode + "-" + formattedCodeCount;
            po.setMaterialCode(generatedMaterialCode);
            
            // 验证生成的物资代码是否重复
            int codeCountCheck = mapper.countByMaterialCode(generatedMaterialCode, null);
            if (codeCountCheck > 0) {
                throw new BusinessRuntimeException("生成的物资代码已存在，请重试");
            }
            
            // 更新三级分类的codeCount为加1后的值
            categoryService.updateCodeCount(dto.getCategoryId(), newCodeCount);
            
            // 设置默认状态为启用
            if (po.getStatus() == null || po.getStatus().trim().isEmpty()) {
                po.setStatus("0");
            }
            mapper.insert(po);
            // 关联文件
            if (dto.getFileIds() != null && !dto.getFileIds().isEmpty()) {
                sysFileService.saveFileBusRelation(dto.getFileIds(), po.getId());
            }
        } else {
            // 修改
            // 修改时验证物资代码是否重复（如果编码不为空）
            if (dto.getMaterialCode() != null && !dto.getMaterialCode().trim().isEmpty()) {
                int count = mapper.countByMaterialCode(dto.getMaterialCode(), dto.getId());
                if (count > 0) {
                    throw new BusinessRuntimeException("物资代码已存在");
                }
            }
            mapper.update(po);
            // 关联文件
            if (dto.getFileIds() != null && !dto.getFileIds().isEmpty()) {
                sysFileService.saveFileBusRelation(dto.getFileIds(), po.getId());
            }
        }
    }

    /**
     * 删除物资代码
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteById(Long id) {
        EMaterialCodePO po = new EMaterialCodePO();
        po.setId(id);
        mapper.deleteById(po);
    }

    /**
     * 查询所有物资代码列表（无分页，用于下拉选择）
     */
    @Override
    public List<EMaterialCodeDTO> getAllList() {
        return mapper.selectAllList();
    }
}

