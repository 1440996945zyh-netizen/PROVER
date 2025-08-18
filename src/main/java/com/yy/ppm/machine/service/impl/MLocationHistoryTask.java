package com.yy.ppm.machine.service.impl;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.machine.bean.dto.MLocationHistoryDTO;
import com.yy.ppm.machine.bean.dto.MLocationHistorySearchDTO;
import com.yy.ppm.machine.controller.MLocationHistoryController;
import com.yy.ppm.machine.mapper.MLocationHistoryMapper;
import com.yy.ppm.machine.service.MLocationHistoryService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * @author czk
 * @version 1.0.0
 * @ClassName 车辆历史表(MLocationHistory)ServiceImpl
 * @Description
 * @createTime 2023年10月25日 10:46:00
 */

@Configuration
@EnableScheduling
public class MLocationHistoryTask{

    @Autowired
    private MLocationHistoryService mLocationHistoryService;
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MLocationHistoryController.class);

    /**
     * 定时删除
     * @param
     * @return 是否成功
     */
    @Scheduled(cron="0 0 0 * * ?")
    public void delete() {
//        LOGGER.enter("定时任务开始删除车辆历史数据开始");
        mLocationHistoryService.delete();
//        LOGGER.exit("定时任务开始删除车辆历史数据退出");
    }

}

