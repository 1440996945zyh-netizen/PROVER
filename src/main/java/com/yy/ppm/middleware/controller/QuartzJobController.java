package com.yy.ppm.middleware.controller;

import com.yy.common.enums.Response;
import com.yy.common.page.Pages;
import com.yy.ppm.system.bean.dto.HttpJobDetailDTO;
import com.yy.ppm.system.bean.dto.HttpJobDetailSearchDTO;
import com.yy.ppm.system.bean.dto.HttpJobLogsDTO;
import com.yy.ppm.system.bean.dto.HttpJobLogsSearchDTO;
import com.yy.ppm.middleware.service.QuartzJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/internal/quartzJob")
@Validated
public class QuartzJobController {

    @Autowired
    private QuartzJobService quartzJobService;

    /**
     * 添加http类型job
     * @param quartzJobDTO
     */
    @PostMapping("/addPostJsonJob")
    public Map<String, Object> addPostJsonJob(@RequestBody HttpJobDetailDTO quartzJobDTO) {
        int count = quartzJobService.addHttpJob(quartzJobDTO);
        return Response.SUCCESS.newBuilder().out(count > 0 ? "新增成功" : "新增失败").toResult();
    }

    /**
     * 查看正在进行的http类型job
     * @param httpJobDetailSearchDTO
     * @return
     */
    @GetMapping("/getJobs")
    public Map<String, Object> getJobs(HttpJobDetailSearchDTO httpJobDetailSearchDTO) {
        Pages<HttpJobDetailDTO> result = quartzJobService.getHttpJobs(httpJobDetailSearchDTO);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 查看历史http类型job
     *
     * @param httpJobDetailSearchDTO
     * @return
     */
    @GetMapping("/historyJobs")
    public  Map<String, Object> getHistoryJobs(HttpJobDetailSearchDTO httpJobDetailSearchDTO) {
        Pages<HttpJobDetailDTO> result = quartzJobService.getHistoryHttpJobs(httpJobDetailSearchDTO);
        return Response.SUCCESS.newBuilder().toResult(result);

    }

    /**
     * 查看http类型的job执行记录
     *
     * @param httpJobLogsSearchDTO
     * @return
     */
    @GetMapping("/jobLogs")
    public Map<String, Object> getJobLogs(HttpJobLogsSearchDTO httpJobLogsSearchDTO) {
        Pages<HttpJobLogsDTO> result = quartzJobService.getHttpJobLogs(httpJobLogsSearchDTO);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 暂停任务
     *
     * @param jobName
     * @param jobGroup
     */
    @PostMapping("/pause")
    public Map<String, Object> pauseJob(@RequestParam(name = "jobName") String jobName,
                             @RequestParam(name = "jobGroup") String jobGroup) {

        quartzJobService.pauseJob(jobName, jobGroup);

        return Response.SUCCESS.newBuilder().out(true ? "暂停成功" : "暂停失败").toResult();
    }

    /**
     * 恢复任务
     *
     * @param jobName
     * @param jobGroup
     */
    @PostMapping("/resume")
    public Map<String, Object> resumeJob(@RequestParam(name = "jobName") String jobName,
                              @RequestParam(name = "jobGroup") String jobGroup) {

        quartzJobService.resumeJob(jobName, jobGroup);
        return Response.SUCCESS.newBuilder().out(true ? "恢复成功" : "恢复失败").toResult();
    }

    /**
     * 删除任务
     *
     * @param jobName
     * @param jobGroup
     */
    @DeleteMapping("/delete")
    public Map<String, Object> deleteJob(@RequestParam(name = "jobName") String jobName,
                              @RequestParam(name = "jobGroup") String jobGroup) {

        quartzJobService.deleteJob(jobName, jobGroup);
        return Response.SUCCESS.newBuilder().out(true ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 更新任务cron表达式
     *
     * @param jobName
     * @param jobGroup
     * @param cronExpression
     */
    @PostMapping("/updateJob")
    public Map<String, Object> updateJob(@RequestParam(name = "jobName") String jobName,
                              @RequestParam(name = "jobGroup") String jobGroup,
                              @RequestParam(name = "cronExpression") String cronExpression) {

        quartzJobService.updateCronExpression(jobName, jobGroup, cronExpression);
        return Response.SUCCESS.newBuilder().out(true ? "更新成功" : "更新失败").toResult();
    }
}
