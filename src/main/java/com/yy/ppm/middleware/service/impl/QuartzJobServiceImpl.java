package com.yy.ppm.middleware.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.alibaba.fastjson.JSON;
import com.yy.common.enums.Constants;
import com.yy.common.page.Pages;
import com.yy.common.util.JobUtil;
import com.yy.common.util.JsonValidUtil;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.framework.quartz.job.HttpGetJob;
import com.yy.framework.quartz.job.HttpPostFormDataJob;
import com.yy.framework.quartz.job.HttpPostJsonJob;
import com.yy.ppm.middleware.service.QuartzJobService;
import com.yy.ppm.middleware.bean.dto.HttpJobDetailDTO;
import com.yy.ppm.middleware.bean.dto.HttpJobDetailSearchDTO;
import com.yy.ppm.middleware.bean.dto.HttpJobLogsDTO;
import com.yy.ppm.middleware.bean.dto.HttpJobLogsSearchDTO;
import com.yy.ppm.middleware.mapper.QuartzJobMapper;
import com.yy.ppm.middleware.service.WsMessageService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class QuartzJobServiceImpl implements QuartzJobService {

    @Resource
    private JobUtil jobUtil;
    @Resource
    private Snowflake snowflake;
    @Resource
    private QuartzJobMapper quartzJobMapper;

    private final Scheduler scheduler;
    public QuartzJobServiceImpl(Scheduler scheduler){
        this.scheduler = scheduler;
    }
    /**
     * 添加http类型job
     *
     * @param quartzJobDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addHttpJob(HttpJobDetailDTO quartzJobDTO) {
        // 校验重复
        if (quartzJobMapper.selectByJobNameAndJobGroup(quartzJobDTO.getJobName(), quartzJobDTO.getJobGroup()) != null) {
            //通过jobName和jobGroup确保任务的唯一性
            throw new BusinessRuntimeException("任务名称重复!");
        }

        HttpJobDetailDTO httpJobDetails = new HttpJobDetailDTO();
        httpJobDetails.setId(snowflake.nextId());
        httpJobDetails.setJobName(quartzJobDTO.getJobName());
        httpJobDetails.setJobGroup(quartzJobDTO.getJobGroup());
        httpJobDetails.setDescription(quartzJobDTO.getDescription());
        httpJobDetails.setRequestType(quartzJobDTO.getRequestType());
        httpJobDetails.setHttpUrl(quartzJobDTO.getHttpUrl());
        if (!JsonValidUtil.isJson(quartzJobDTO.getHttpParams())) {
            throw new BusinessRuntimeException("请将请求参数转为合法的json字符串!");
        }

        Map<String, Object> jobParamsMap = new HashMap<>();
        jobParamsMap.put(Constants.URL, quartzJobDTO.getHttpUrl());
        jobParamsMap.put(Constants.PARAMS, quartzJobDTO.getHttpParams());

        JobDetail jobDetail = null;
        //根据不同类型的job构建job信息
        switch (quartzJobDTO.getRequestType()) {
            //postJson
            case Constants.POST_JSON:
                jobDetail = JobBuilder.newJob(HttpPostJsonJob.class)
                        .withIdentity(quartzJobDTO.getJobName(), quartzJobDTO.getJobGroup())
                        .build();

                //jsonStr的参数直接用
                if (StringUtils.isNotEmpty(quartzJobDTO.getHttpParams())) {
                    httpJobDetails.setHttpParams(quartzJobDTO.getHttpParams());
                }
                break;

            //postFormData
            case Constants.POST_FORM_DATA:
                jobDetail = JobBuilder.newJob(HttpPostFormDataJob.class)
                        .withIdentity(quartzJobDTO.getJobName(), quartzJobDTO.getJobGroup())
                        .build();

                //jsonStr参数转为formData的Map
                Map<String, Object> formDataParamMap;
                if (StringUtils.isEmpty(quartzJobDTO.getHttpParams())) {
                    formDataParamMap = null;
                } else {
                    formDataParamMap = JSON.parseObject(quartzJobDTO.getHttpParams(), Map.class);
                    httpJobDetails.setHttpParams(formDataParamMap.toString());
                }
                jobParamsMap.put(Constants.PARAMS, formDataParamMap);

                break;

            //get
            case Constants.GET:
                jobDetail = JobBuilder.newJob(HttpGetJob.class)
                        .withIdentity(quartzJobDTO.getJobName(), quartzJobDTO.getJobGroup())
                        .build();

                //jsonStr参数转为formData的Map
                Map<String, Object> paramMap;
                if (StringUtils.isEmpty(quartzJobDTO.getHttpParams())) {
                    paramMap = null;
                } else {
                    paramMap = JSON.parseObject(quartzJobDTO.getHttpParams(), Map.class);
                    httpJobDetails.setHttpParams(paramMap.toString());
                }
                jobParamsMap.put(Constants.PARAMS, paramMap);

                break;
        }

        //任务信息
        jobDetail.getJobDataMap().putAll(jobParamsMap);
        jobDetail.getJobDataMap().put(Constants.REQUEST_TYPE, quartzJobDTO.getRequestType());

        //表达式调度构建器(即任务执行的时间)
        CronScheduleBuilder scheduleBuilder;
        try {
            scheduleBuilder = CronScheduleBuilder.cronSchedule(quartzJobDTO.getCronExpression());
        } catch (Exception e) {
            throw new BusinessRuntimeException("Cron表达式不合法!");
        }

        TriggerKey triggerKey = jobUtil.getTriggerKeyByJob(quartzJobDTO.getJobName(), quartzJobDTO.getJobGroup());

        //构建一个trigger
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .startNow()
                .withSchedule(scheduleBuilder).build();

        try {
            // 调度容器设置JobDetail和Trigger
            scheduler.scheduleJob(jobDetail, trigger);
            // 启动
            if (!scheduler.isShutdown()) {
                scheduler.start();
            }
        } catch (Exception e) {
            throw new RuntimeException("Schedule Exception.", e);
        }

        return quartzJobMapper.insertJobDetail(httpJobDetails);
    }

    /**
     * 查看正在进行的http类型job
     * @param httpJobDetailSearchDTO
     * @return
     */
    @Override
    public Pages<HttpJobDetailDTO> getHttpJobs(HttpJobDetailSearchDTO httpJobDetailSearchDTO) {
        Pages<HttpJobDetailDTO> httpJobDetailVOList = PageHelperUtils.
                limit(httpJobDetailSearchDTO, () -> quartzJobMapper.selectHttpJobs(httpJobDetailSearchDTO));

        for (HttpJobDetailDTO httpJobDetailDTO : httpJobDetailVOList.getPages()) {
            //设置jobStatusInfo
            String jobStatusInfo = jobUtil.getJobStatusInfo(httpJobDetailDTO.getJobName(), httpJobDetailDTO.getJobGroup());
            httpJobDetailDTO.setJobStatusInfo(jobStatusInfo);

            //任务状态正常，根据cron表达式计算下次运行时间
            if (StringUtils.equals(jobStatusInfo, Constants.JOB_STATUS_NORMAL)) {
                httpJobDetailDTO.setNextFireTime(jobUtil.getNextFireDate(httpJobDetailDTO.getCronExpression()));
            }
        }

        return httpJobDetailVOList;
    }

    /**
     * 查看历史http类型job
     *
     * @param httpJobDetailSearchDTO
     * @return
     */
    @Override
    public Pages<HttpJobDetailDTO> getHistoryHttpJobs(HttpJobDetailSearchDTO httpJobDetailSearchDTO) {
        Pages<HttpJobDetailDTO> httpJobDetailVOList = PageHelperUtils.
                limit(httpJobDetailSearchDTO, () -> quartzJobMapper.selectHistoryHttpJobs(httpJobDetailSearchDTO));

        return httpJobDetailVOList;
    }

    /**
     * 查看http类型的job执行记录
     *
     * @param httpJobLogsSearchDTO
     * @return
     */
    @Override
    public Pages<HttpJobLogsDTO> getHttpJobLogs(HttpJobLogsSearchDTO httpJobLogsSearchDTO) {
        Pages<HttpJobLogsDTO> httpJobDetailVOList = PageHelperUtils.
                limit(httpJobLogsSearchDTO, () -> quartzJobMapper.selectHttpJobLogs(httpJobLogsSearchDTO));

        return httpJobDetailVOList;
    }



    /**
     * 暂停任务
     *
     * @param jobName
     * @param jobGroup
     */
    @Override
    public void pauseJob(String jobName, String jobGroup) {
        String jobStatusInfo = jobUtil.getJobStatusInfo(jobName, jobGroup);
        if (StringUtils.equals(jobStatusInfo, Constants.JOB_STATUS_PAUSED)) {
            throw new RuntimeException("当前任务已是暂停状态!");
        }
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        try {
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 恢复任务
     *
     * @param jobName
     * @param jobGroup
     */
    @Override
    public void resumeJob(String jobName, String jobGroup) {
        String jobStatusInfo = jobUtil.getJobStatusInfo(jobName, jobGroup);
        if (!StringUtils.equals(jobStatusInfo, Constants.JOB_STATUS_PAUSED)) {
            throw new RuntimeException("任务仅在暂停状态时才能恢复!");
        }
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        try {
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 删除任务
     *
     * @param jobName
     * @param jobGroup
     */
    @Override
    public void deleteJob(String jobName, String jobGroup) {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        TriggerKey triggerKey = jobUtil.getTriggerKeyByJob(jobName, jobGroup);
        try {
            scheduler.pauseTrigger(triggerKey);
            scheduler.unscheduleJob(triggerKey);
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 更新任务cron表达式
     *
     * @param jobName
     * @param jobGroup
     * @param cronExpression
     */
    @Override
    public void updateCronExpression(String jobName, String jobGroup, String cronExpression) {
        TriggerKey triggerKey = jobUtil.getTriggerKeyByJob(jobName, jobGroup);

        //表达式调度构建器(即任务执行的时间)
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

        //按新的cronExpression重新构建trigger
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(scheduleBuilder).build();
        try {
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

    }
}
