package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.ppm.equipment.bean.dto.EPatrolTaskSearchDTO;
import com.yy.ppm.equipment.bean.po.EPatrolTaskPO;
import com.yy.ppm.equipment.bean.po.EPatrolTaskSubPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 巡检任务 Mapper 接口
 *
 * @author system
 */
@Mapper
public interface EPatrolTaskMapper {

    /**
     * 分页查询巡检任务列表 (PC)
     *
     * @param searchDTO 查询参数
     * @return 分页结果
     */
    Page<EPatrolTaskPO> selectTaskList(EPatrolTaskSearchDTO searchDTO);

    /**
     * 根据主表ID获取详情 (子表列表 - 分页)
     *
     * @param searchDTO 查询参数 (包含 id 字段)
     * @return 分页结果
     */
    <T> Page<T> selectSubTaskPage(EPatrolTaskSearchDTO searchDTO);
}
