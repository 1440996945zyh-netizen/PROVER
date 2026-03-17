package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EContractInfoDTO;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EContractInfoMapper {

    <T> Page<T> getList(EContractInfoDTO searchDTO);

    EContractInfoDTO getById(EContractInfoDTO searchDTO);

    @Edit
    void insert(EContractInfoDTO po);

    @Edit
    void update(EContractInfoDTO po);

    void deleteById(@Param("id") Long id);

}
