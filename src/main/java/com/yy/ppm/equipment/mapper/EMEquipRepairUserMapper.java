package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.bean.dto.EMaintRepairUserOptionDTO;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairUserDTO;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairUserDetailDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EMEquipRepairUserMapper {

    <T> Page<T> getList(EMEquipRepairUserDTO searchDTO);

    EMEquipRepairUserDTO getById(EMEquipRepairUserDTO searchDTO);

    @Edit
    void insert(EMEquipRepairUserDTO po);

    @Edit
    void update(EMEquipRepairUserDTO po);

    void deleteById(@Param("id") Long id);

    List<EMEquipRepairUserDetailDTO> getUserDetailList(Long id);

    List<EMaintRepairUserOptionDTO> getRepairUserListByMaintOrgId(@Param("maintOrgId") Long maintOrgId);


    @Edit
    void insertUserDetail(@Param("list") List<EMEquipRepairUserDetailDTO> list);

    void deleteUserDetail(Long id);
}
