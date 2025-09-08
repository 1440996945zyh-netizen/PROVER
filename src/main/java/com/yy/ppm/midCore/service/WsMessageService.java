package com.yy.ppm.midCore.service;

import com.yy.ppm.midCore.bean.po.WsOfflineMessagePO;

import java.util.List;

public interface WsMessageService {
    /**
     * 新增离线消息
     * @param wsOfflineMessagePO
     */
    int add(WsOfflineMessagePO wsOfflineMessagePO);

    /**
     * 按照接收人查询离线消息
     */
    List<WsOfflineMessagePO> getMessageByReceiver(String receiverAccount);

    /**
     * 标记消息为已发送
     */
    int updateIsSent(WsOfflineMessagePO wsOfflineMessagePO);
}
