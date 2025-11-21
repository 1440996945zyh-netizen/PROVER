package com.yy.ppm.flowable.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yy.ppm.flowable.bean.po.SysCustomForm;
import com.yy.ppm.flowable.bean.po.SysDeployForm;
import com.yy.ppm.flowable.bean.po.SysForm;

import java.util.List;

/**
 * @Description: 流程实例关联表单
 * @Author: hukang
 * @Date:   2025-11-07
 * @Version: V1.0
 */

public interface ISysDeployFormService extends IService<SysDeployForm> {
    /**
     * 批量插入(包含限制条数，目前是100条)
     * @param
     * @return
     */
    boolean insertBatch(List<SysDeployForm> deployFormList);

    /**
     * 查询流程挂着的表单
     * @param
     * @return
     */
    SysForm selectSysDeployFormByDeployId(String deploymentId);

    /**
     * 查询流程挂着的自定义表单
     * @param
     * @return
     */
    SysCustomForm selectSysCustomFormByDeployId(String deployId);
}
