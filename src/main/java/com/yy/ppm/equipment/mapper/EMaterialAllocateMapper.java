package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaterialAllocateDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialAllocateSearchDTO;
import com.yy.ppm.equipment.bean.po.EMaterialAllocatePO;
import org.apache.ibatis.annotations.Param;

/**
 * 物资调拨Mapper
 * @author system
 */
public interface EMaterialAllocateMapper {

    Page<EMaterialAllocateDTO> getList(EMaterialAllocateSearchDTO searchDTO);

    EMaterialAllocateDTO getById(@Param("id") Long id);

    EMaterialAllocatePO selectById(@Param("id") Long id);

    @Edit
    int insert(EMaterialAllocatePO po);

    @Edit
    int update(EMaterialAllocatePO po);

    @Edit
    int updateExecuteResult(EMaterialAllocatePO po);

    int deleteById(@Param("id") Long id);

    Long getBusinessDataIdByProcessInstanceId(@Param("processInstanceId") String processInstanceId);
}
