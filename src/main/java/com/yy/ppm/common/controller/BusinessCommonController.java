package com.yy.ppm.common.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.str.StringUtil;
import com.yy.ppm.common.service.BusinessCommonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 业务类通用接口
 */
@RestController
@RequestMapping(value = "/api/internal/businessCommon")
public class BusinessCommonController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(BusinessCommonController.class);

    @Resource
    private BusinessCommonService businessCommonService;

    /**
     * 获取计划派工信息
     *
     * @author yy
     * @param workPlanId
     */
    @GetMapping(value = "/getEquipmentDispatch")
    public Map<String, Object> getEquipmentDispatch(Long workPlanId, String workPositionCode)  {
        final String methodName = "getEquipmentDispatch";
        LOGGER.enter(methodName, "传入计划日期，返回配工机械[start], workPlanId: " + StringUtil.getString(workPlanId) + ", workPositionCode: " + StringUtil.getString(workPositionCode));

        List<Map<String, Object>> res = businessCommonService.getWorkPlanEquipmentDispatch(workPlanId, workPositionCode);

        LOGGER.exit(methodName, "传入计划日期，返回配工机械[end], result: " + res);
        return Response.SUCCESS.newBuilder().toResult(res);
    }

}
