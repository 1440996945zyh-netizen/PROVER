package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialWarningRecordDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarningRecordSearchDTO;

/**
 * 预警消息
 */
public interface EMaterialWarningRecordService {

    /**
     * 主列表
     */
    Pages<EMaterialWarningRecordDTO> getList(EMaterialWarningRecordSearchDTO searchDTO);

    /**
     * 详情
     */
    EMaterialWarningRecordDTO getById(Long id);

    /**
     * 批量处理
     */
    void handleBatch(EMaterialWarningRecordDTO dto);
}
