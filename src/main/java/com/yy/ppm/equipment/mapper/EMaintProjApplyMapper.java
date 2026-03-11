package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.bean.dto.EMaintProjApplyDTO;
import com.yy.ppm.equipment.bean.po.EMaintProjApplyQuotaPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EMaintProjApplyMapper {

    <T> Page<T> getList(EMaintProjApplyDTO searchDTO);

    EMaintProjApplyDTO getById(EMaintProjApplyDTO searchDTO);

    @Edit
    void insert(EMaintProjApplyDTO po);



    void deleteById(@Param("id") Long id);


    /**
     * 根据所属单位ID和outType查询维修单位列表
     */
    List<EMEquipRepairContractDTO> getByCompanyIdAndOutType(@Param("useCompanyId") Long useCompanyId, @Param("outType") String outType);

    List<EMaintProjApplyQuotaPO> getApplyQuataList(Long id);

    @Edit
    void insertApplyQuata(EMaintProjApplyQuotaPO item);

    void deleteApplyQuata(Long id);
}
