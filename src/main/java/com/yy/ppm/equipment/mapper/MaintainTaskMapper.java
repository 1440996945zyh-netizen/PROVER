package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.MaintainTaskDTO;
import com.yy.ppm.equipment.bean.po.MaintainTaskItemPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MaintainTaskMapper {
    <T> Page<T> getList(MaintainTaskDTO searchDTO);
    <T> Page<T> getListAPP(MaintainTaskDTO searchDTO);

    <T> Page<T> getById(MaintainTaskDTO searchDTO);

    List<Map<String, Object>> getInstitutionById(MaintainTaskDTO searchDTO);

    List<Map<String, Object>> getUnitById(MaintainTaskDTO searchDTO);

    List<MaintainTaskItemPO> getTaskItemById(MaintainTaskDTO searchDTO);

    @Edit
    void updateTaskItemList(@Param("list") List<MaintainTaskItemPO> list);

    int getTaskItemCountById(Long equipTaskId);

    @Edit
    void updateTaskById(@Param("equipTaskId") Long equipTaskId, @Param("status") Integer status);
}
