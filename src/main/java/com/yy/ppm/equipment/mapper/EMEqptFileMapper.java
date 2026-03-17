package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMEqptFileDTO;
import com.yy.ppm.equipment.bean.dto.EMEqptFileSearchDTO;
import com.yy.ppm.equipment.bean.po.EMEqptFilePO;
import org.apache.ibatis.annotations.Param;

/**
 * 设备资料文件Mapper接口
 * @author system
 */
public interface EMEqptFileMapper {

    /**
     * 查询设备资料文件列表（分页）
     */
    Page<EMEqptFileDTO> selectList(EMEqptFileSearchDTO searchDTO);

    /**
     * 根据ID查询设备资料文件
     */
    EMEqptFileDTO selectById(@Param("id") Long id);

    /**
     * 新增设备资料文件
     */
    @Edit
    void insert(EMEqptFilePO po);

    /**
     * 修改设备资料文件
     */
    @Edit
    void update(EMEqptFilePO po);

    /**
     * 删除设备资料文件
     */
    @Edit
    void deleteById(@Param("id") Long id);
}

