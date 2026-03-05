package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentOperationDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentOperationPO;

import java.util.List;

public interface EMEquipRepairContractService {
    Pages<EMEquipRepairContractDTO> getList(EMEquipRepairContractDTO searchDTO, PageParameter parameter);

    EMEquipRepairContractDTO getById(EMEquipRepairContractDTO searchDTO);
    List<EMEquipRepairContractDTO> queryUnitName(EMEquipRepairContractDTO searchDTO);

    void save(EMEquipRepairContractDTO po);

    void delete(Long id);

    /**
     * 根据设备ID和outType查询维修单位列表
     * @param equipId 设备ID
     * @param outType 类型（1:内部 2:外部）
     * @return 维修单位列表（包含EXTERNAL_COMPANY_ID和UNIT_NAME）
     */
    List<EMEquipRepairContractDTO> getRepairContractByEquipId(Long equipId, String outType);
}
