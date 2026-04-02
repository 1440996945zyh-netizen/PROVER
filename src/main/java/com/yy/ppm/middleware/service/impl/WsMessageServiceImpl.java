package com.yy.ppm.middleware.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.ppm.middleware.bean.po.WsOfflineMessagePO;
import com.yy.ppm.middleware.mapper.WsMessageMapper;
import com.yy.ppm.middleware.service.WsMessageService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WsMessageServiceImpl implements WsMessageService {

    @Resource
    WsMessageMapper wsMessageMapper;

    /**
     * 新增离线消息
     * @param wsOfflineMessagePO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(WsOfflineMessagePO wsOfflineMessagePO) {
        return wsMessageMapper.add(wsOfflineMessagePO);
    }

    /**
     * 按照接收人查询离线消息
     */
    @Override
    public List<WsOfflineMessagePO> getMessageByReceiver(String receiverAccount) {
        return wsMessageMapper.getMessageByReceiver(receiverAccount);
    }

    /**
     * 标记消息为已发送
     */
    @Override
    public int updateIsSent(WsOfflineMessagePO wsOfflineMessagePO) {
        return wsMessageMapper.updateIsSent(wsOfflineMessagePO);
    }

    /**
     * 根据组织单位、岗位信息查询用户账号
     * @param deptId
     * @param postCode
     * @return
     */
    @Override
    public List<String> getUserAccounts(String deptId, String postCode) {
        return wsMessageMapper.getUserAccounts(deptId,postCode);
    }


}
