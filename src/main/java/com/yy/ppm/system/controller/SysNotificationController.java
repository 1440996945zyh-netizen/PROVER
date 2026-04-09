package com.yy.ppm.system.controller;

import com.yy.common.log.MicroLogger;
import com.yy.common.enums.Response;
import com.yy.ppm.system.bean.dto.SysNotificationDTO;
import com.yy.ppm.system.service.SysNotificationService;
import com.yy.common.util.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "消息通知")
@RestController
@RequestMapping("/system/notification")
public class SysNotificationController {

    private static final MicroLogger LOGGER = new MicroLogger(SysNotificationController.class);

    @Autowired
    private SysNotificationService sysNotificationService;

    @GetMapping("/list")
    public Map<String, Object> getList() {
        final String methodName = "SysNotificationController:list";
        LOGGER.enter(methodName + "[start]");

        Long loginUserId = SecurityUtils.getLoginUserId();
        List<SysNotificationDTO> list = sysNotificationService.getList(loginUserId);

        LOGGER.exit(methodName + "[result]" + (list != null ? list.size() : 0));
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    @GetMapping("/count")
    public Map<String, Object> count() {
        final String methodName = "SysNotificationController:count";
        LOGGER.enter(methodName + "[start]");

        Long loginUserId = SecurityUtils.getLoginUserId();
        int count = sysNotificationService.countByReceiverId(loginUserId);

        LOGGER.exit(methodName + "[result]" + count);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(count);
    }
}
