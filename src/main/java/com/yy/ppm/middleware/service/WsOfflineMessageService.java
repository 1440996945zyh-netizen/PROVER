package com.yy.ppm.middleware.service;

import com.yy.common.page.Pages;
import com.yy.ppm.middleware.bean.po.WsOfflineMessagePO;
import com.yy.ppm.system.bean.dto.HttpJobDetailDTO;
import com.yy.ppm.system.bean.dto.HttpJobDetailSearchDTO;
import com.yy.ppm.system.bean.dto.HttpJobLogsDTO;
import com.yy.ppm.system.bean.dto.HttpJobLogsSearchDTO;

import java.util.List;

public interface WsOfflineMessageService {
    /**
     * 新增离线消息
     * @param wsOfflineMessagePO
     */
    int add(WsOfflineMessagePO wsOfflineMessagePO);

    /**
     * 按照接收人查询离线消息
     */
    List<WsOfflineMessagePO> getMessageByReceiver(Long receiverId);

    /**
     * 标记消息为已发送
     */
    int updateIsSent(WsOfflineMessagePO wsOfflineMessagePO);
}
