package com.yy.ppm.produce.controller;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.enums.Response;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.produce.bean.dto.TPrdSalaryResultDTO;
import com.yy.ppm.produce.bean.dto.salary.SalaryQueryDTO;
import com.yy.ppm.produce.bean.dto.salary.SalaryQueryExamineDTO;
import com.yy.ppm.produce.bean.po.TPrdSalaryPO;
import com.yy.ppm.produce.service.TPrdSalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-04 17:01
 */
@RestController
@RequestMapping("/api/external/salary")
@Validated
public class TPrdSalaryController {

    @Autowired
    private TPrdSalaryService tPrdSalaryService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private SecurityUtils securityUtils;
    private static final String REDIS_PREFIX_EXAMINE = "com.yy.ppm.produce.service.impl.examine";
    private static final Integer REDIS_EXPIRE_SECONDS = 900; // 默认超期时间15分钟
    /**
     * 计件工资列表
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/listSalary")
    public Map<String, Object> listSalary(SalaryQueryDTO query, PageParameter parameter) {
        Pages<TPrdSalaryResultDTO> result = tPrdSalaryService.listSalary(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 计件工资列表
     *
     * @param query
     * @param
     * @return
     */
    @GetMapping("/getSalarySum")
    public Map<String, Object> getSalarySum(SalaryQueryDTO query) {
        Map<String,String> result = tPrdSalaryService.getSalarySum(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    /**
     * 获取审核日志
     *
     * @param query
     * @param
     * @return
     */
    @GetMapping("/getExamineLog")
    public Map<String, Object> getExamineLog(SalaryQueryDTO query) {
        String errMsg = tPrdSalaryService.getExamineLog(query);
        return Response.SUCCESS.newBuilder().toResult(errMsg);
    }

    /**
     * 计件作业量导出
     *
     * @param query
     * @param response
     * @return
     */
    @GetMapping("/exportSalary")
    public void exportSalary(SalaryQueryDTO query, HttpServletResponse response) {
        ResponseUtils.compliantWithExcel(response, "计件作业量");
        try {
            byte[] bytes = tPrdSalaryService.exportSalary(query);
            try {
                response.getOutputStream().write(bytes);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Exception e) {
            ResponseUtils.resetCompliant(response);
            throw e;
        }
    }

    /**
     * 计件工资导出
     *
     * @param query
     * @param response
     * @return
     */
    @GetMapping("/exportSalary2")
    public void exportSalary2(SalaryQueryDTO query, HttpServletResponse response) {
        ResponseUtils.compliantWithExcel(response, "计件工资");
        try {
            byte[] bytes = tPrdSalaryService.exportSalary2(query);
            try {
                response.getOutputStream().write(bytes);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Exception e) {
            ResponseUtils.resetCompliant(response);
            throw e;
        }
    }

    /**
     * 计件审核/取消审核
     */
    @GetMapping("/examine")
    public Map<String, Object> examine(SalaryQueryExamineDTO dto) {
        if (dto.getDeptId() == null) {
            throw new BusinessRuntimeException("请选择要审核的部门");
        }
        if (dto.getStartDate() == null) {
            throw new BusinessRuntimeException("请先选择开始日期,在查询之后再次重新审核");
        }
        if (dto.getEndDate() == null) {
            throw new BusinessRuntimeException("请先选择结束日期，在查询之后再次重新审核");
        }
        long start = System.currentTimeMillis();
        String redisKey = REDIS_PREFIX_EXAMINE + ":" + dto.getAuditMonth() + ":" + dto.getDeptId();
        UserInfo userInfo = securityUtils.getUserInfo();
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(redisKey, String.valueOf(start), REDIS_EXPIRE_SECONDS, TimeUnit.SECONDS);
        if (!flag) {
            throw new BusinessRuntimeException("正在处理中，请稍后。。。");
        }
        redisTemplate.expire(redisKey, REDIS_EXPIRE_SECONDS, TimeUnit.SECONDS);
        tPrdSalaryService.examine(dto, userInfo, redisKey);
        return Response.SUCCESS.newBuilder().out("处理中，请稍后查看").toResult();
    }

    /**
     * 计件审核/取消审核
     */
    @GetMapping("/examineHr")
    public Map<String, Object> examineHr(SalaryQueryExamineDTO dto) {
        String redisKey = REDIS_PREFIX_EXAMINE + ":" + dto.getAuditMonth();
        Set<String> keys = redisTemplate.keys(redisKey + ":*");
        if (!CollectionUtils.isEmpty(keys)) {
            throw new BusinessRuntimeException("正在处理中，请稍后。。。");
        }
        tPrdSalaryService.examineHr(dto);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }
}
