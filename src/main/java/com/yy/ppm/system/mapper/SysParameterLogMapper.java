package com.yy.ppm.system.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.system.bean.dto.SysParameterLogDTO;

import java.util.List;

public interface SysParameterLogMapper {
    List<SysParameterLogDTO> getList(SysParameterLogDTO sysParameterLogDTO);

    @Edit
    void insert(SysParameterLogDTO dto);

    @Edit
    void update(SysParameterLogDTO dto);
}
