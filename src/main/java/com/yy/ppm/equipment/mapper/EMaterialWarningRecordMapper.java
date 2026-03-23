package com.yy.ppm.equipment.mapper;
import com.yy.framework.annotation.Edit;
import org.apache.ibatis.annotations.Param;
import com.yy.ppm.equipment.bean.po.EMaterialWarningRecordPO;

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
}