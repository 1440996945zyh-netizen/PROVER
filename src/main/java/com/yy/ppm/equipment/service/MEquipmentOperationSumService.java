package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.MEquipmentOperationDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentOperationPO;

public interface MEquipmentOperationSumService {
    Pages<MEquipmentOperationDTO> getList(MEquipmentOperationDTO searchDTO, PageParameter parameter);


}
