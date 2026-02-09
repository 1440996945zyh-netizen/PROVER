package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.MEquipmentOperationDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentOperationPO;

public interface MEquipmentOperationService {
    Pages<MEquipmentOperationPO> getList(MEquipmentOperationDTO searchDTO, PageParameter parameter);

    MEquipmentOperationPO getById(MEquipmentOperationDTO searchDTO);

    void save(MEquipmentOperationPO po);

    void delete(Long id);
}
