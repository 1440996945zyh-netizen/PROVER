package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.MEquipmentOperationDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentOperationPO;
import org.apache.ibatis.annotations.Param;

public interface MEquipmentOperationSumMapper {

    <T> Page<T> getList(MEquipmentOperationDTO searchDTO);


}
