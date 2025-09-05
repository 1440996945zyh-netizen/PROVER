package com.yy.ppm.middleware.controller;

import com.yy.common.enums.Response;
import com.yy.common.page.Pages;
import com.yy.ppm.middleware.bean.po.WsOfflineMessagePO;
import com.yy.ppm.middleware.service.QuartzJobService;
import com.yy.ppm.middleware.service.WsOfflineMessageService;
import com.yy.ppm.system.bean.dto.*;
import com.yy.ppm.system.bean.dto.HttpJobDetailDTO;
import com.yy.ppm.system.bean.dto.HttpJobDetailSearchDTO;
import com.yy.ppm.system.bean.dto.HttpJobLogsDTO;
import com.yy.ppm.system.bean.dto.HttpJobLogsSearchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/internal/wsOfflineMessage")
@Validated
public class WsOfflineMessageController {

    @Autowired
    private WsOfflineMessageService wsOfflineMessageService;

    /**
     * 新增离线消息
     * @param wsOfflineMessagePO
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody WsOfflineMessagePO wsOfflineMessagePO) {
        int count = wsOfflineMessageService.add(wsOfflineMessagePO);
        return Response.SUCCESS.newBuilder().out(count > 0 ? "新增成功" : "新增失败").toResult();
    }

    /**
     * 按照接收人查询离线消息
     */
    @GetMapping("/getMessageByReceiver")
    public Map<String,Object> getMessageByReceiver(@RequestParam("receiverId") String receiverAccount) {
        List<WsOfflineMessagePO> wsOfflineMessagePOList = wsOfflineMessageService.getMessageByReceiver(receiverAccount);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(wsOfflineMessagePOList);
    }

    /**
     * 标记消息为已发送
     */
    @PostMapping("updateIsSent")
    public Map<String, Object> updateIsSent(@RequestBody WsOfflineMessagePO wsOfflineMessagePO) {
        int count = wsOfflineMessageService.updateIsSent(wsOfflineMessagePO);
        return Response.SUCCESS.newBuilder().out(count > 0 ? "修改成功" : "修改失败").toResult();
    }
}
