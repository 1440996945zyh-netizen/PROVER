package com.yy.ppm.produce.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Maps;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.produce.mapper.TPoundMapper;
import com.yy.ppm.produce.service.TPoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author czk
 * @version 1.0.0
 * @ClassName 车辆历史表(CalculateOutPayTask)ServiceImpl
 * @Description
 * @createTime 2024年06月24日 10:46:00
 */

@Configuration
@EnableScheduling
public class PoundToPortStorageTask {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(PoundToPortStorageTask.class);

    @Autowired
    private TPoundService tPoundService;
    @Resource
    private TPoundMapper tPoundMapper;
    /**
     * 定时
     * @param
     * @return 是否成功
     */

//    @Scheduled(cron="0 * * * * ?")
    @Scheduled(cron="0 20 8 * * ?")
    public void poundToPortStorageTask() {
        try{
            LOGGER.enter("8:20==>地磅港存更新==开始==>");
            String startDate = DateUtil.format(DateUtil.offsetDay(new DateTime(), -2),"yyyy-MM-dd HH:mm:ss");
            String endDate = DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss");
            Map<String,Object> params = Maps.newHashMap();
            params.put("startDate",startDate);
            params.put("endDate",endDate);
            params.put("noteId",null);
            tPoundService.updatePortStage(tPoundMapper.getTallyByParams(params));
            LOGGER.info("8:20==>地磅港存更新==结束==>");
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }
}

