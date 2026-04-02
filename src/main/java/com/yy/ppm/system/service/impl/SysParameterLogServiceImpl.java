package com.yy.ppm.system.service.impl;

import com.yy.common.log.MicroLogger;
import com.yy.ppm.system.bean.dto.SysParameterLogDTO;
import com.yy.ppm.system.mapper.SysParameterLogMapper;
import com.yy.ppm.system.service.SysParameterLogService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class SysParameterLogServiceImpl  implements SysParameterLogService {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(SysParameterLogServiceImpl.class);
    @Resource
    private SysParameterLogMapper sysParameterLogMapper;

    @Override
    public List<SysParameterLogDTO> getList(SysParameterLogDTO sysParameterLogDTO) {
        return sysParameterLogMapper.getList(sysParameterLogDTO);
    }


}
