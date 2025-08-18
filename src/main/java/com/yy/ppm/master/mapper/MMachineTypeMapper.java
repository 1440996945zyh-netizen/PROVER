package com.yy.ppm.master.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MMachineTypeDTO;
import com.yy.ppm.master.bean.po.MMachineTypeModelPO;
import com.yy.ppm.master.bean.po.MMachineTypePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 机械类型Mapper接口
 * */
public interface MMachineTypeMapper{

    /**
     * 查询机械类型
     * */
    Page<MMachineTypeDTO> selectBMachineType(@Param("name") String name);


    /**
     * 根据id查询机械类型
     * */
    MMachineTypeDTO selectBMachineTypeById(@Param("id") String id);

    /**
     * 根据机械类型id查询所有机械型号
     * */
    List<MMachineTypeModelPO> selectBMachineTypeModel(@Param("macTypeCode") String macTypeCode);

    /**
     * 增加机械类型
     * */
    @Edit
    void insertBMachineType(MMachineTypePO bo);

    Integer getCountByType(MMachineTypeModelPO po);
    /**
     * 增加机械型号
     * */
    void insertBMachineTypeModel(MMachineTypeModelPO so);

    /**
     * 修改机械型号
     * */
    @Edit
    void updateBMachineType(MMachineTypeDTO bo);

    /**
     * 根据机械类型code删除机械型号
     * */
    void deleteBMachineTypeModelByTypeCode(@Param("typeCode") String typeCode);

    /**
     * 删除机械型号
     * */
    void deleteBMachineTypeModel(@Param("id")Long id);

    /**
     * 删除机械类型
     * */
    void deleteBMachineType(@Param("id") Long id);

    /**
     * 查询机械类型列表
     * */
    List<MMachineTypeModelPO> listBMachineTypeModel();


    String getMaxTypeCode();

    String getMaxModelCode(@Param("typeCode") String typeCode);

    List<MMachineTypeModelPO> getMacModelByTypeCode(@Param("macTypeCode") String id);
}
