package com.yy.ppm.system.controller;


import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.system.bean.dto.SysParameterLogDTO;
import com.yy.ppm.system.service.SysParameterLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/internal/sysParameterLog")
public class SysParameterLogController {
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(SysParameterLogController.class);

    /**
     * 服务对象
     */
    private final SysParameterLogService sysParameterLogService;

    public SysParameterLogController(SysParameterLogService sysParameterLogService){
        this.sysParameterLogService = sysParameterLogService;
    }

    /**
     * 根据实体类筛选数据列表
     *
     * @return 统一数据封装
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(SysParameterLogDTO sysParameterLogDTO) {
        final String methodName = "SysParameterLogController:getList";
        LOGGER.enter( methodName + "[start]");

        List<SysParameterLogDTO> sysParameterList = sysParameterLogService.getList(sysParameterLogDTO);

        LOGGER.exit(methodName + "result:" + sysParameterList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(sysParameterList);
    }

}
