package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EPatrolTaskSearchDTO;
import com.yy.ppm.equipment.bean.po.EPatrolTaskPO;
import com.yy.ppm.equipment.bean.po.EPatrolTaskSubPO;

import java.util.List;

/**
 * 巡检任务 Service 接口
 *
 * @author system
 */
public interface EPatrolTaskService {

    /**
     * 分页查询巡检任务列表 (PC)
     */
    Pages<EPatrolTaskPO> getTaskList(EPatrolTaskSearchDTO searchDTO, PageParameter parameter);

    /**
     * 根据主表ID获取详情 (子表列表 - 分页)
     */
    Pages<EPatrolTaskSubPO> getSubTaskPage(EPatrolTaskSearchDTO searchDTO, PageParameter parameter);
}
