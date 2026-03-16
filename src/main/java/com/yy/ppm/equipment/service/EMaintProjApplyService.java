package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.bean.dto.EMaintProjApplyDTO;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;

import java.util.List;

public interface EMaintProjApplyService {
    Pages<EMaintProjApplyDTO> getList(EMaintProjApplyDTO searchDTO, PageParameter parameter);

    EMaintProjApplyDTO getById(EMaintProjApplyDTO searchDTO);


    void save(EMaintProjApplyDTO po);

    void delete(Long id);
    void deleteProJect(Long id);

    void submit(BpmProcessInstanceDTO dto);


}
