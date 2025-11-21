package com.yy.ppm.flowable.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yy.ppm.flowable.bean.po.SysForm;
import com.yy.ppm.flowable.mapper.SysFormMapper;
import com.yy.ppm.flowable.service.ISysFormService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * @Description: 系统流程表单
 * @Author: hukang
 * @Date:   2025-11-11
 * @Version: V1.0
 */
@Service
public class SysFormServiceImpl extends ServiceImpl<SysFormMapper, SysForm> implements ISysFormService {
    @Resource
    SysFormMapper sysFormMapper;

    @Override
    public SysForm selectSysFormById(String formId) {
        return sysFormMapper.selectSysFormById(formId);
    }
}
