package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairUserDTO;

public interface EMEquipRepairUserService {
    Pages<EMEquipRepairUserDTO> getList(EMEquipRepairUserDTO searchDTO, PageParameter parameter);

    EMEquipRepairUserDTO getById(EMEquipRepairUserDTO searchDTO);

    void save(EMEquipRepairUserDTO po);

    void delete(Long id);
}
