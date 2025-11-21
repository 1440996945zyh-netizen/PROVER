package com.yy.ppm.flowable.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yy.ppm.flowable.bean.po.SysForm;

public interface ISysFormService extends IService<SysForm> {
    SysForm selectSysFormById(String formId);
}
