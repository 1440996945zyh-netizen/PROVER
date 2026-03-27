package com.yy.ppm.equipment.service.impl;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.equipment.bean.dto.EPatrolTaskSearchDTO;
import com.yy.ppm.equipment.bean.po.EPatrolTaskPO;
import com.yy.ppm.equipment.bean.po.EPatrolTaskSubPO;
import com.yy.ppm.equipment.mapper.EPatrolTaskMapper;
import com.yy.ppm.equipment.service.EPatrolTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 巡检任务 Service 实现
 *
 * @author system
 */
@Service
public class EPatrolTaskServiceImpl implements EPatrolTaskService {

    @Autowired
    private EPatrolTaskMapper mapper;

    @Override
    public Pages<EPatrolTaskPO> getTaskList(EPatrolTaskSearchDTO searchDTO, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> mapper.selectTaskList(searchDTO));
    }

    @Override
    public Pages<EPatrolTaskSubPO> getSubTaskPage(EPatrolTaskSearchDTO searchDTO, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> mapper.selectSubTaskPage(searchDTO));
    }
}
