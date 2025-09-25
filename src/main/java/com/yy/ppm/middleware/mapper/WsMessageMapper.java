package com.yy.ppm.middleware.mapper;


import com.yy.ppm.middleware.bean.po.WsOfflineMessagePO;

import java.util.List;

public interface WsMessageMapper {
    /**
     * 新增离线消息
     * @param wsOfflineMessagePO
     * @return
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

    /**
     * 根据组织单位、岗位信息查询用户账号
     * @param deptId
     * @param postCode
     * @return
     */
    List<String> getUserAccounts(String deptId, String postCode);
}
