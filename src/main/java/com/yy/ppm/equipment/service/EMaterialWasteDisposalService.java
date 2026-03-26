package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWasteDisposalDTO;

import java.util.List;

public interface EMaterialWasteDisposalService {
    Pages<EMaterialWasteDisposalDTO> getList(EMaterialWasteDisposalDTO searchDTO, PageParameter parameter);

    EMaterialWasteDisposalDTO getById(EMaterialWasteDisposalDTO searchDTO);



    void save(EMaterialWasteDisposalDTO po);

    void delete(Long id);


}
