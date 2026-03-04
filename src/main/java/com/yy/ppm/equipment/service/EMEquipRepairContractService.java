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
}
