package com.yy.ppm.produce.mapper;

import com.yy.ppm.produce.bean.po.TPrdSalaryLogPO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.Map;

/**
 * @ClassName 计件工资审核日志
 * @author wangxd
 * @version 1.0.0
 * @Description
 * @createTime 2023年12月25日 18:21:00
 */
public interface TPrdSalaryLogMapper {

    int addLog(TPrdSalaryLogPO po);

    TPrdSalaryLogPO getLog(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("deptId") Long deptId);

    int deleteLog(@Param("startDate") Date startDate, @Param("endDate") Date endDate,@Param("deptId") Long deptId);
}
