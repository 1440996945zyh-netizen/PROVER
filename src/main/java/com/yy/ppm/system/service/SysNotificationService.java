package com.yy.ppm.system.service;

import com.yy.ppm.system.bean.dto.SysNotificationDTO;

import java.util.List;

public interface SysNotificationService {

    /**
     * 发送消息通知（保存到数据库并通过WebSocket推送）
     * @param title 消息标题
     * @param content 消息内容
     * @param receiverIds 接收人ID列表
     */
    void sendNotification(String title, String content, List<Long> receiverIds,Long businessId);

    /**
     * 查询消息列表（最近15条）
     * @param receiverId 接收人ID
     * @return 消息列表
     */
    List<SysNotificationDTO> getList(Long receiverId);

    /**
     * 查询消息数量
     * @param receiverId 接收人ID
     * @return 未读消息数量
     */
    int countByReceiverId(Long receiverId);
}
