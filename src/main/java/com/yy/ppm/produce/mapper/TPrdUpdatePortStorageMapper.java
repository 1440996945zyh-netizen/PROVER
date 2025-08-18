package com.yy.ppm.produce.mapper;

import com.github.pagehelper.Page;
import com.yy.ppm.business.bean.dto.TPrdUpdatePortStorageReqDTO;
import com.yy.ppm.produce.bean.dto.workTicket.PoundToPortStorageDTO;
import java.util.Map;
import java.util.List;

public interface TPrdUpdatePortStorageMapper {
    Page<PoundToPortStorageDTO> getTallyByParams(TPrdUpdatePortStorageReqDTO query);

    Page<PoundToPortStorageDTO> getUpdatePortStoragePage(TPrdUpdatePortStorageReqDTO query);

}
