package com.yy.ppm.equipment.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.EMaterialWarningRecordDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarningRecordSearchDTO;
import com.yy.ppm.equipment.mapper.EMaterialWarningRecordMapper;
import com.yy.ppm.equipment.service.EMaterialWarningRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 预警消息
 */
@Service
public class EMaterialWarningRecordServiceImpl implements EMaterialWarningRecordService {

    @Autowired
    private EMaterialWarningRecordMapper eMaterialWarningRecordMapper;

    /**
     * 主列表
     */
    @Override
    public Pages<EMaterialWarningRecordDTO> getList(EMaterialWarningRecordSearchDTO searchDTO) {
        return PageHelperUtils.limit(searchDTO, () -> eMaterialWarningRecordMapper.selectList(searchDTO));
    }

    /**
     * 详情
     */
    @Override
    public EMaterialWarningRecordDTO getById(Long id) {
        return eMaterialWarningRecordMapper.selectById(id);
    }

    /**
     * 批量处理
     */
    @Override
    public void handleBatch(EMaterialWarningRecordDTO dto) {
        if (dto == null || dto.getIds() == null || dto.getIds().isEmpty()) {
            throw new BusinessRuntimeException("请选择要处理的数据");
        }

        eMaterialWarningRecordMapper.handleBatch(dto);
    }
}