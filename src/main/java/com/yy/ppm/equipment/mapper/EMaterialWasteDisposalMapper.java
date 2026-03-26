package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWasteDisposalDTO;
import com.yy.ppm.equipment.bean.po.EMaterialWasteDisposalSubPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EMaterialWasteDisposalMapper {

    <T> Page<T> getList(EMaterialWasteDisposalDTO searchDTO);

    EMaterialWasteDisposalDTO getById(EMaterialWasteDisposalDTO searchDTO);

    @Edit
    void insert(EMaterialWasteDisposalDTO po);

    @Edit
    void update(EMaterialWasteDisposalDTO po);


    List<EMaterialWasteDisposalSubPO> getDetail(Long id);

    void insertDetail(@Param("list") List<EMaterialWasteDisposalSubPO> list);

    void deleteDetail(Long id);

    void deleteById(Long id);
}
