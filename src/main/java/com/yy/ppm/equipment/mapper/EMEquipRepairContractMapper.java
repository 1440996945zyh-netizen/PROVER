package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentOperationDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentOperationPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EMEquipRepairContractMapper {

    <T> Page<T> getList(EMEquipRepairContractDTO searchDTO);

    EMEquipRepairContractDTO getById(EMEquipRepairContractDTO searchDTO);

    @Edit
    void insert(EMEquipRepairContractDTO po);

    @Edit
    void update(EMEquipRepairContractDTO po);

    void deleteById(@Param("id") Long id);

    List<EMEquipRepairContractDTO> queryUnitName(EMEquipRepairContractDTO searchDTO);

    /**
     * 根据所属单位ID和outType查询维修单位列表
     */
    List<EMEquipRepairContractDTO> getByCompanyIdAndOutType(@Param("useCompanyId") Long useCompanyId, @Param("outType") String outType);

    int getUser(Long id);

    EMEquipRepairContractDTO getOutTypeNum();

}
