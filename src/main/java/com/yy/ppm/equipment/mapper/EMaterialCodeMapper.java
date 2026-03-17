package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaintInfoDTO;
import com.yy.ppm.equipment.bean.dto.EMaintInfoSearchDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialCodeDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialCodeSearchDTO;
import com.yy.ppm.equipment.bean.po.EMaterialCodePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物资代码Mapper接口
 * @author system
 */
public interface EMaterialCodeMapper {

    /**
     * 查询物资代码列表
     */
    Page<EMaterialCodeDTO> selectList(EMaterialCodeSearchDTO searchDTO);

    /**
     * 根据ID查询物资代码
     */
    EMaterialCodeDTO selectById(@Param("id") Long id);

    /**
     * 新增物资代码
     */
    @Edit
    void insert(EMaterialCodePO po);

    /**
     * 修改物资代码
     */
    @Edit
    void update(EMaterialCodePO po);

    /**
     * 删除物资代码（逻辑删除）
     */
    @Edit
    void deleteById(EMaterialCodePO po);

    /**
     * 检查物资代码是否重复
     */
    int countByMaterialCode(@Param("materialCode") String materialCode,
                           @Param("id") Long id);

    /**
     * 查询所有物资代码（无分页，用于树结构）
     */
    List<EMaterialCodeDTO> selectAllList();

    /**
     * 查询所有三级类别（用于树结构）
     */
    List<com.yy.ppm.equipment.bean.dto.EMaterialCategoryDTO> selectLevel3Categories();
}

