package com.yy.ppm.system.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.enums.WebsocketEnum;
import com.yy.common.ws.WebSocketUtils;
import com.yy.ppm.system.bean.dto.SysNotificationDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.bean.po.SysNotificationPO;
import com.yy.ppm.system.mapper.SysNotificationMapper;
import com.yy.ppm.system.service.SysNotificationService;
import com.yy.ppm.system.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysNotificationServiceImpl implements SysNotificationService {

    @Resource
    private SysNotificationMapper mapper;

    @Resource
    private Snowflake snowflake;

    @Resource
    private SysUserService sysUserService;

    @Override
    public void sendNotification(String title, String content, List<Long> receiverIds,Long businessId) {
        if (receiverIds == null || receiverIds.isEmpty()) {
            return;
        }

        String receiverIdStr = receiverIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        Date now = new Date();
        SysNotificationPO po = new SysNotificationPO();
        po.setId(snowflake.nextId());
        po.setTitle(title);
        po.setContent(content);
        po.setReceiverId(receiverIdStr);
        po.setCreateTime(now);
        po.setBusinessId(businessId);

        mapper.insert(po);

        for (Long receiverId : receiverIds) {
            sendWebSocketMessage(receiverId, title, content);
        }
    }

    private void sendWebSocketMessage(Long receiverId, String title, String content) {
        SysUserDTO user = sysUserService.getById(receiverId);
        if (user == null || user.getUserAccount() == null) {
            return;
        }

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("contentType", WebsocketEnum.PERSONAL_TYPE.getCode());
        messageMap.put("receiverAccount", user.getUserAccount());
        messageMap.put("senderAccount", "SYSTEM");
        messageMap.put("content", content);
        messageMap.put("title", title);
        messageMap.put("mesShowType", WebsocketEnum.NOTICE_TYPE.getCode());

        WebSocketUtils.sendMessage(messageMap);
    }

    @Override
    public List<SysNotificationDTO> getList(Long receiverId) {
        return mapper.getList(receiverId, 15);
    }

    @Override
    public int countByReceiverId(Long receiverId) {
        return mapper.countByReceiverId(receiverId);
    }
}
