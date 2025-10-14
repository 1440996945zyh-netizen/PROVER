package com.yy.framework.quartz.job;
import cn.hutool.core.lang.Snowflake;
import com.yy.common.enums.Constants;
import com.yy.common.util.HttpClientUtil;
import com.yy.ppm.middleware.bean.po.HttpJobLogsPO;
import com.yy.ppm.middleware.mapper.QuartzJobMapper;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

import java.util.Date;
import java.util.Map;

@DisallowConcurrentExecution
public class HttpPostJsonJob implements Job {

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
        String jsonParam = (String) jobParamsMap.get(Constants.PARAMS);

        HttpJobLogsPO httpJobLogs = new HttpJobLogsPO();
        httpJobLogs.setId(snowflake.nextId());
        httpJobLogs.setJobName(jobName);
        httpJobLogs.setJobGroup(jobGroup);
        httpJobLogs.setRequestType(requestType);
        httpJobLogs.setHttpUrl(url);
        httpJobLogs.setHttpParams(jsonParam);
        httpJobLogs.setFireTime(new Date());


        String result = HttpClientUtil.postJson(url, jsonParam);
        httpJobLogs.setResult(result);

        logger.info("Success in execute [{}_{}]", jobName, jobGroup);

        quartzJobMapper.insertJobLogs(httpJobLogs);
    }

}
