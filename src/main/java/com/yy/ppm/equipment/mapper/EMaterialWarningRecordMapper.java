package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaterialWarningRecordDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarningRecordSearchDTO;
import com.yy.ppm.equipment.bean.po.EMaterialWarningRecordPO;
import org.apache.ibatis.annotations.Param;

/**
 * 预警消息Mapper
 */
public interface EMaterialWarningRecordMapper {

    /**
     * 新增预警消息
     */
    @Edit
    void add(EMaterialWarningRecordPO po);

    /**
     * 查当前物资是否已有未处理预警
     */
    Long countUnhandledByMaterialId(@Param("materialId") Long materialId);

    /**
     * 主列表
     */
    Page<EMaterialWarningRecordDTO> selectList(EMaterialWarningRecordSearchDTO searchDTO);

    /**
     * 详情
     */
    EMaterialWarningRecordDTO selectById(@Param("id") Long id);

    /**
     * 批量处理
     */
    @Edit
    void handleBatch(EMaterialWarningRecordDTO dto);
}