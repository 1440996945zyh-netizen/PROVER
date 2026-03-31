package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EPatrolTaskSearchDTO;
import com.yy.ppm.equipment.bean.po.EPatrolTaskPO;
import com.yy.ppm.equipment.bean.po.EPatrolTaskSubPO;

import java.util.List;
import java.util.Map;

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

    /**
     * 查询巡检任务APP（分页）
     */
    Pages<EPatrolTaskPO> getListAPP(EPatrolTaskSearchDTO searchDTO, PageParameter parameter);

    /**
     * 根据任务ID查询设备
     */
    List<Map<String, Object>> getEquipmentById(EPatrolTaskSearchDTO searchDTO);

    /**
     * 根据任务ID和设备ID查询巡检任务子表
     */
    List<EPatrolTaskSubPO> getTaskItemById(EPatrolTaskSearchDTO searchDTO);

    /**
     * APP保存巡检任务执行详情
     */
    void save(List<EPatrolTaskSubPO> list);
}
