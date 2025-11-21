package com.yy.ppm.flowable.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yy.ppm.flowable.bean.po.SysForm;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description: 系统流程表单
 * @Author: hukang
 * @Date:   2025-11-07
 * @Version: V1.0
 */
public interface SysFormMapper extends BaseMapper<SysForm> {
    SysForm selectSysFormById(String formId);

}
