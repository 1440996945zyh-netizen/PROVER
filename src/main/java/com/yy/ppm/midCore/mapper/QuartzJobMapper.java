package com.yy.ppm.midCore.mapper;


import com.github.pagehelper.Page;
import com.yy.ppm.system.bean.dto.HttpJobDetailDTO;
import com.yy.ppm.system.bean.dto.HttpJobDetailSearchDTO;
import com.yy.ppm.system.bean.dto.HttpJobLogsDTO;
import com.yy.ppm.system.bean.dto.HttpJobLogsSearchDTO;
import com.yy.ppm.system.bean.po.HttpJobLogsPO;

public interface QuartzJobMapper {

    /**
     * 根据任务名称、任务分组查询相关任务
     * @param jobName
     * @param jobGroup
     * @return
     */
    HttpJobDetailDTO selectByJobNameAndJobGroup(String jobName, String jobGroup);


    /**
     * 保存请求与请求结果
     * @param httpJobLogs
     * @return
     */
    int insertJobLogs(HttpJobLogsPO httpJobLogs);

    /**
     * 保存任务详情信息
     * @param quartzJobPO
     * @return
     */
    int insertJobDetail(HttpJobDetailDTO quartzJobPO);

    /**
     * 查看正在进行的http类型job
     * @param httpJobDetailSearchDTO
     * @return
     */
    Page<HttpJobDetailDTO> selectHttpJobs(HttpJobDetailSearchDTO httpJobDetailSearchDTO);

    /**
     * 查看历史http类型job
     * @param httpJobDetailSearchDTO
     * @return
     */
    Page<HttpJobDetailDTO> selectHistoryHttpJobs(HttpJobDetailSearchDTO httpJobDetailSearchDTO);

    /**
     * 查看http类型的job执行记录
     * @param httpJobLogsSearchDTO
     * @return
     */
    Page<HttpJobLogsDTO> selectHttpJobLogs(HttpJobLogsSearchDTO httpJobLogsSearchDTO);
}
