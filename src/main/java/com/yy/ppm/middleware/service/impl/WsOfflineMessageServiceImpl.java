package com.yy.ppm.middleware.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.alibaba.fastjson.JSON;
import com.yy.common.enums.Constants;
import com.yy.common.page.Pages;
import com.yy.common.util.JobUtil;
import com.yy.common.util.JsonValidUtil;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.quartz.job.HttpGetJob;
import com.yy.framework.quartz.job.HttpPostFormDataJob;
import com.yy.framework.quartz.job.HttpPostJsonJob;
import com.yy.ppm.middleware.bean.po.WsOfflineMessagePO;
import com.yy.ppm.middleware.mapper.QuartzJobMapper;
import com.yy.ppm.middleware.mapper.WsOfflineMessageMapper;
import com.yy.ppm.middleware.service.QuartzJobService;
import com.yy.ppm.middleware.service.WsOfflineMessageService;
import com.yy.ppm.system.bean.dto.HttpJobDetailDTO;
import com.yy.ppm.system.bean.dto.HttpJobDetailSearchDTO;
import com.yy.ppm.system.bean.dto.HttpJobLogsDTO;
import com.yy.ppm.system.bean.dto.HttpJobLogsSearchDTO;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WsOfflineMessageServiceImpl implements WsOfflineMessageService {

    @Resource
    WsOfflineMessageMapper wsOfflineMessageMapper;

    @Autowired
    private Snowflake snowflake;

    /**
     * 新增离线消息
     * @param wsOfflineMessagePO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(WsOfflineMessagePO wsOfflineMessagePO) {
        return wsOfflineMessageMapper.add(wsOfflineMessagePO);
    }

    /**
     * 按照接收人查询离线消息
     */
    @Override
    public List<WsOfflineMessagePO> getMessageByReceiver(String receiverAccount) {
        return wsOfflineMessageMapper.getMessageByReceiver(receiverAccount);
    }

    /**
     * 标记消息为已发送
     */
    @Override
    public int updateIsSent(WsOfflineMessagePO wsOfflineMessagePO) {
        return wsOfflineMessageMapper.updateIsSent(wsOfflineMessagePO);
    }


}
