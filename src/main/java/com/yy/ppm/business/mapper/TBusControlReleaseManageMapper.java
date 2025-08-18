package com.yy.ppm.business.mapper;

import com.github.pagehelper.Page;
import com.yy.ppm.business.bean.dto.TBusReleaseManageDTO;
import com.yy.ppm.business.bean.dto.TBusReleaseManageSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TBusControlReleaseManageMapper {

    Page<TBusReleaseManageDTO> getList(TBusReleaseManageSearchDTO searchDTO);

    TBusReleaseManageDTO getById(@Param("id") Long id);

    boolean updateRelease(TBusReleaseManageDTO tBusReleaseManageDTO);

    int getTrustById(@Param("id") Long id);
}
