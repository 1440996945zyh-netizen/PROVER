package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaintInfoLogDTO;
import com.yy.ppm.equipment.bean.po.EMaintInfoLogPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备维修操作日志 Mapper
 */
public interface EMaintInfoLogMapper {

    /**
     * 新增操作日志
     */
    @Edit
    void insert(EMaintInfoLogPO po);

    /**
     * 根据维修主表 ID 查询操作日志
     */
    List<EMaintInfoLogDTO> selectListByMaintInfoId(@Param("maintInfoId") Long maintInfoId);
}
