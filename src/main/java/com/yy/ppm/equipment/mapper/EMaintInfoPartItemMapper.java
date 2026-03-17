package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaintInfoPartItemDTO;
import com.yy.ppm.equipment.bean.po.EMaintInfoPartItemPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备维保派工部位部件子表Mapper
 */
public interface EMaintInfoPartItemMapper {

    /**
     * 根据维保主表ID查询部位部件子表
     */
    List<EMaintInfoPartItemDTO> selectListByMaintInfoId(@Param("maintInfoId") Long maintInfoId);

    /**
     * 批量新增部位部件子表
     */
    @Edit
    void insertBatch(@Param("list") List<EMaintInfoPartItemPO> list);

    /**
     * 根据维保主表ID逻辑删除子表
     */
    @Edit
    void logicDeleteByMaintInfoId(EMaintInfoPartItemPO po);
}

