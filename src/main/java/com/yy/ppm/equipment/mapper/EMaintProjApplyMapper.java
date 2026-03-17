package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaintProjApplyDTO;
import com.yy.ppm.equipment.bean.po.EMaintProjApplyQuotaPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EMaintProjApplyMapper {

    <T> Page<T> getList(EMaintProjApplyDTO searchDTO);

    EMaintProjApplyDTO getById(EMaintProjApplyDTO searchDTO);

    @Edit
    void insert(EMaintProjApplyDTO po);



    void deleteById(@Param("id") Long id, @Param("status") String code);


    List<EMaintProjApplyQuotaPO> getApplyQuataList(Long id);

    @Edit
    void insertApplyQuata(EMaintProjApplyQuotaPO item);

    void deleteApplyQuata(Long id);

    void update(EMaintProjApplyDTO po);

    /**
     * 根据申请单号修改结算状态
     */
    void updateIsSettlement(@Param("appNumber") String appNumber, @Param("isSettlement") String isSettlement);

    void deleteApply(Long id);
}
