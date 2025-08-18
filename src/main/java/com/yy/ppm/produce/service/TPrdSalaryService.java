package com.yy.ppm.produce.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.produce.bean.dto.TPrdSalaryResultDTO;
import com.yy.ppm.produce.bean.dto.salary.SalaryQueryDTO;
import com.yy.ppm.produce.bean.dto.salary.SalaryQueryExamineDTO;
import com.yy.ppm.produce.bean.po.TPrdSalaryPO;

import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-04 17:03
 */
public interface TPrdSalaryService {
    /**
     * 分页查询
     * @param query
     * @param parameter
     * @return
     */
    Pages<TPrdSalaryResultDTO> listSalary(SalaryQueryDTO query, PageParameter parameter);

    Map<String,String> getSalarySum(SalaryQueryDTO query);

    /**
     * 导出
     * @param query
     * @return
     */
    byte[] exportSalary(SalaryQueryDTO query);
    /**
     * 导出计件工资
     * @param query
     * @return
     */
    byte[] exportSalary2(SalaryQueryDTO query);

    /**
     * 生产审核
     * @param dto
     * @param userInfo
     * @param redisKey
     */
    void examine(SalaryQueryExamineDTO dto, UserInfo userInfo, String redisKey);

    /**
     * hr审核
     * @param dto
     */
    void examineHr(SalaryQueryExamineDTO dto);
    /**
     * 获取生产审核日志
     * @param query
     * @return
     */
    String getExamineLog(SalaryQueryDTO query);
}
