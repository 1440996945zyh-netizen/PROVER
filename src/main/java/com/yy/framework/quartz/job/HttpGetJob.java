package com.yy.framework.quartz.job;
import cn.hutool.core.lang.Snowflake;
import com.yy.common.enums.Constants;
import com.yy.common.util.HttpClientUtil;
import com.yy.ppm.common.enums.ScheduleTaskEnum;
import com.yy.ppm.middleware.bean.po.HttpJobLogsPO;
import com.yy.ppm.middleware.mapper.QuartzJobMapper;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@DisallowConcurrentExecution
public class HttpGetJob implements Job {

    private static final Logger logger = LogManager.getLogger(HttpPostJsonJob.class);

    @Resource
    private QuartzJobMapper quartzJobMapper;

    @Resource
    private Snowflake snowflake;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        String jobName = jobDetail.getKey().getName();
        String jobGroup = jobDetail.getKey().getGroup();

        Map<String, Object> jobParamsMap = jobDetail.getJobDataMap();


        String requestType = (String) jobParamsMap.get(Constants.REQUEST_TYPE);
        String url = (String) jobParamsMap.get(Constants.URL);
        Map<String, Object> paramMap = (Map) jobParamsMap.get(Constants.PARAMS);

        //加定时任务验证（防止恶意调用）
        if(paramMap==null){
            paramMap = new HashMap<>();
        }
        paramMap.put(ScheduleTaskEnum.SCHEDULE_TASK_KEY.getKey(), ScheduleTaskEnum.SCHEDULE_TASK_KEY.getValue());

        HttpJobLogsPO httpJobLogs = new HttpJobLogsPO();
        httpJobLogs.setId(snowflake.nextId());
        httpJobLogs.setJobName(jobName);
        httpJobLogs.setJobGroup(jobGroup);
        httpJobLogs.setRequestType(requestType);
        httpJobLogs.setHttpUrl(url);
        httpJobLogs.setFireTime(new Date());
        if (null != paramMap && paramMap.size() > 0) {
            httpJobLogs.setHttpParams(paramMap.toString());
        }

        String result = HttpClientUtil.getMap(url, paramMap);
        httpJobLogs.setResult(result);

        logger.info("Success in execute [{}_{}]", jobName, jobGroup);

        quartzJobMapper.insertJobLogs(httpJobLogs);
    }

}
