package com.yy.ppm.produce.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.dto.TPrdVehicleReleaseDTO;
import com.yy.ppm.produce.bean.po.TPrdVehicleReleasePO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TPrdVehicleReleaseMapper {

    Page<TPrdVehicleReleasePO> getList(TPrdVehicleReleaseDTO query);

    Page<TPrdVehicleReleasePO> getSubList(TPrdVehicleReleaseDTO query);
    int updateStatus(@Param("taskNo") String taskNo);

    @Edit
    int auditStatusById(TPrdVehicleReleaseDTO dto);
    @Edit
    int auditRevokeStatusById(TPrdVehicleReleaseDTO dto);

    TPrdVehicleReleaseDTO getById(Long id);
}
