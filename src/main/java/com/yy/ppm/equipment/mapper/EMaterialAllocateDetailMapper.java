package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaterialAllocateDetailDTO;
import com.yy.ppm.equipment.bean.po.EMaterialAllocateDetailPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物资调拨明细Mapper
 * @author system
 */
public interface EMaterialAllocateDetailMapper {

    List<EMaterialAllocateDetailDTO> selectListByAllocateId(@Param("allocateId") Long allocateId);

    @Edit
    int batchInsert(@Param("list") List<EMaterialAllocateDetailPO> list);

    @Edit
    int updateRelationIds(@Param("id") Long id,
                          @Param("outDetailId") Long outDetailId,
                          @Param("inDetailId") Long inDetailId,
                          @Param("updateBy") Long updateBy,
                          @Param("updateByName") String updateByName);

    int deleteByAllocateId(@Param("allocateId") Long allocateId);
}
