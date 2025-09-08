package com.yy.ppm.midCore.controller;

import com.yy.common.enums.Response;
import com.yy.ppm.midCore.bean.po.WsOfflineMessagePO;
import com.yy.ppm.midCore.service.WsMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/internal/wsOfflineMessage")
@Validated
public class WsMessageController {

    @Autowired
    private WsMessageService wsMessageService;

    /**
     * 新增离线消息
     * @param wsOfflineMessagePO
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody WsOfflineMessagePO wsOfflineMessagePO) {
        int count = wsMessageService.add(wsOfflineMessagePO);
        return Response.SUCCESS.newBuilder().out(count > 0 ? "新增成功" : "新增失败").toResult();
    }

    /**
     * 按照接收人查询离线消息
     */
    @GetMapping("/getMessageByReceiver")
    public Map<String,Object> getMessageByReceiver(@RequestParam("receiverId") String receiverAccount) {
        List<WsOfflineMessagePO> wsOfflineMessagePOList = wsMessageService.getMessageByReceiver(receiverAccount);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(wsOfflineMessagePOList);
    }

    /**
     * 标记消息为已发送
     */
    @PostMapping("updateIsSent")
    public Map<String, Object> updateIsSent(@RequestBody WsOfflineMessagePO wsOfflineMessagePO) {
        int count = wsMessageService.updateIsSent(wsOfflineMessagePO);
        return Response.SUCCESS.newBuilder().out(count > 0 ? "修改成功" : "修改失败").toResult();
    }
}
