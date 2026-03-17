package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaintHourFeedbackDTO;
import com.yy.ppm.equipment.bean.po.EMaintHourFeedbackPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备维修作业工时反馈子表Mapper
 */
public interface EMaintHourFeedbackMapper {

    /**
     * 根据维保主表ID查询作业工时反馈列表
     */
    List<EMaintHourFeedbackDTO> selectListByMaintInfoId(@Param("maintInfoId") Long maintInfoId);

    /**
     * 批量新增作业工时反馈
     */
    @Edit
    void insertBatch(@Param("list") List<EMaintHourFeedbackPO> list);

    /**
     * 根据维保主表ID逻辑删除作业工时反馈
     */
    @Edit
    void logicDeleteByMaintInfoId(EMaintHourFeedbackPO po);
}
