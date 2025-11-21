package com.yy.ppm.flowable.service.impl;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.yy.ppm.flowable.bean.po.SysCustomForm;
import com.yy.ppm.flowable.bean.po.SysDeployForm;
import com.yy.ppm.flowable.bean.po.SysForm;
import com.yy.ppm.flowable.mapper.SysDeployFormMapper;
import com.yy.ppm.flowable.service.ISysDeployFormService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
/**
 * @Description: 流程实例关联表单
 * @Author: hukang
 * @Date:   2025-11-07
 * @Version: V1.0
 */
@Service
public class SysDeployFormServiceImpl extends ServiceImpl<SysDeployFormMapper, SysDeployForm> implements ISysDeployFormService {
    @Resource
    private SysDeployFormMapper sysDeployFormMapper;

    /**
     * 批量插入(包含限制条数，目前是100条)
     * @param
     * @return
     */
    @Override
    public boolean insertBatch(List<SysDeployForm> deployFormList) {
        return this.saveBatch(deployFormList, 100);
    }

    /**
     * 查询流程挂着的表单
     * @param
     * @return
     */
    @Override
    public SysForm selectSysDeployFormByDeployId(String deploymentId) {
        return sysDeployFormMapper.selectSysDeployFormByDeployId(deploymentId);
    }

    /**
     * 查询流程挂着的自定义表单
     * @param
     * @return
     */
    @Override
    public SysCustomForm selectSysCustomFormByDeployId(String deployId) {
        return sysDeployFormMapper.selectSysCustomFormByDeployId(deployId);
    }
}
