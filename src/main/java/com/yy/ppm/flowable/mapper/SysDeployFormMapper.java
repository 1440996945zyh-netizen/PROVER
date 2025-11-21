package com.yy.ppm.flowable.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yy.ppm.flowable.bean.po.SysCustomForm;
import com.yy.ppm.flowable.bean.po.SysDeployForm;
import com.yy.ppm.flowable.bean.po.SysForm;

/**
 * @Description: 流程实例关联表单
 * @Author: hukang
 * @Date:   2025-11-07
 * @Version: V1.0
 */
public interface SysDeployFormMapper extends BaseMapper<SysDeployForm> {
    /**
     * 查询关联表内容
     * @param formId
     * @return
     */
    SysDeployForm selectSysDeployFormByFormId(String formId);

    /**
     * 查询流程挂着的表单
     * @param deployId
     * @return
     */
    SysForm selectSysDeployFormByDeployId(String deployId);

    /**
     * 查询流程挂着的自定义表单
     * @param
     * @return
     */
    SysCustomForm selectSysCustomFormByDeployId(String deployId);
}
