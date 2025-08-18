package com.yy.ppm.system.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.bean.dto.SysParameterLogDTO;
import com.yy.ppm.system.mapper.SysParameterLogMapper;
import com.yy.ppm.system.mapper.SysParameterMapper;
import com.yy.ppm.system.service.SysParameterLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class SysParameterLogServiceImpl  implements SysParameterLogService {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(SysParameterLogServiceImpl.class);

    @Autowired
    private Snowflake snowflake;

    @Resource
    private SysParameterLogMapper sysParameterLogMapper;

    @Override
    public List<SysParameterLogDTO> getList(SysParameterLogDTO sysParameterLogDTO) {
        return sysParameterLogMapper.getList(sysParameterLogDTO);
    }


}
