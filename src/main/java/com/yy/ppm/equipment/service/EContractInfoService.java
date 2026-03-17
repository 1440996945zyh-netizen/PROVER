package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EContractInfoDTO;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;

import java.util.List;

public interface EContractInfoService {
    Pages<EContractInfoDTO> getList(EContractInfoDTO searchDTO, PageParameter parameter);

    EContractInfoDTO getById(EContractInfoDTO searchDTO);


    void save(EContractInfoDTO po);

    void delete(Long id);


}
