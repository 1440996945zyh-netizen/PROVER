package com.yy.ppm.business.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.business.bean.dto.ComprehensiveDTO;
import com.yy.ppm.business.service.ComprehensiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/v1/internal/comprehensiveQuery")
public class ComprehensiveController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(ComprehensiveController.class);

    @Autowired
    private ComprehensiveService comprehensiveService;
    /**
     * 按日期查询详情
     * @param planDate
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(String planDate) {
        final String methodName = "ComprehensiveController:getList";
        LOGGER.enter(methodName + "综合查询 [start]", "date:" + planDate );

        ComprehensiveDTO result = comprehensiveService.getList(planDate);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * 按日期查询详情
     * @param planDate
     * @return
     */
    @GetMapping("/getPieList")
    public Map<String, Object> getPieList(String planDate) {
        final String methodName = "ComprehensiveController:getPieList";
        LOGGER.enter(methodName + "综合查询 [start]", "date:" + planDate );

        List<Map<String, String>> result = comprehensiveService.getPieList(planDate);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}
