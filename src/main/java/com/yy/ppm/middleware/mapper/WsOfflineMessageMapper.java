package com.yy.ppm.middleware.mapper;


import com.github.pagehelper.Page;
import com.yy.ppm.middleware.bean.po.WsOfflineMessagePO;
import com.yy.ppm.system.bean.dto.HttpJobDetailDTO;
import com.yy.ppm.system.bean.dto.HttpJobDetailSearchDTO;
import com.yy.ppm.system.bean.dto.HttpJobLogsDTO;
import com.yy.ppm.system.bean.dto.HttpJobLogsSearchDTO;
import com.yy.ppm.system.bean.po.HttpJobLogsPO;

import java.util.List;

public interface WsOfflineMessageMapper {
    /**
     * 新增离线消息
     * @param wsOfflineMessagePO
     * @return
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
