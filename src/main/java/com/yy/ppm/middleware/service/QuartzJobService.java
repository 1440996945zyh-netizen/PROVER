package com.yy.ppm.middleware.service;

import com.yy.common.page.Pages;
import com.yy.ppm.middleware.bean.dto.HttpJobDetailDTO;
import com.yy.ppm.middleware.bean.dto.HttpJobDetailSearchDTO;
import com.yy.ppm.middleware.bean.dto.HttpJobLogsDTO;
import com.yy.ppm.middleware.bean.dto.HttpJobLogsSearchDTO;

public interface QuartzJobService {
    /**
     * 添加http类型job
     *
     * @param quartzJobDTO
     */
    int addHttpJob(HttpJobDetailDTO quartzJobDTO);

    /**
     * 查看正在进行的http类型job
     * @param httpJobDetailSearchDTO
     * @return
     */
    Pages<HttpJobDetailDTO> getHttpJobs(HttpJobDetailSearchDTO httpJobDetailSearchDTO);

    /**
     * 查看历史http类型job
     *
     * @param httpJobDetailSearchDT
     * @return
     */
    Pages<HttpJobDetailDTO> getHistoryHttpJobs(HttpJobDetailSearchDTO httpJobDetailSearchDT);

    /**
     * 查看http类型的job执行记录
     *
     * @param httpJobLogsSearchDTO
     * @return
     */
    Pages<HttpJobLogsDTO> getHttpJobLogs(HttpJobLogsSearchDTO httpJobLogsSearchDTO);



    /**
     * 暂停任务
     *
     * @param jobName
     * @param jobGroup
     */
    void pauseJob(String jobName, String jobGroup);

    /**
     * 恢复任务
     *
     * @param jobName
     * @param jobGroup
     */
    void resumeJob(String jobName, String jobGroup);

    /**
     * 删除任务
     *
     * @param jobName
     * @param jobGroup
     */
    void deleteJob(String jobName, String jobGroup);

    /**
     * 更新任务cron表达式
     *
     * @param jobName
     * @param jobGroup
     * @param cronExpression
     */
    void updateCronExpression(String jobName, String jobGroup, String cronExpression);
}
