package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.po.EMaterialAllocateHistoryPO;
import org.apache.ibatis.annotations.Param;

/**
 * 物资调拨历史Mapper
 * @author system
 */
public interface EMaterialAllocateHistoryMapper {

    @Edit
    int insert(EMaterialAllocateHistoryPO po);

    int deleteByAllocateId(@Param("allocateId") Long allocateId);
}
