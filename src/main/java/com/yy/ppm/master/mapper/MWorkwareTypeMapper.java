package com.yy.ppm.master.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MWorkwareTypeDTO;
import com.yy.ppm.master.bean.po.MWorkwareTypeModelPO;
import com.yy.ppm.master.bean.po.MWorkwareTypePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工属具类型Mapper接口
 *
 */
public interface MWorkwareTypeMapper {
    /**
     * 查询工属具类型
     * */
    Page<MWorkwareTypeDTO> selectBWorkwareType(@Param("name") String name);

    /**
     * 根据id查询工属具类型
     * */
    MWorkwareTypeDTO selectBWorkwareTypeById(@Param("id") Long id);

    /**
     * 根据工属具类型id查询所有工属具型号
     * */
    List<MWorkwareTypeModelPO> selectBWorkwareTypeModel(@Param("typeCode") String typeCode);

    /**
     * 增加工属具类型
     * */
    @Edit
    void insertBWorkwareType(MWorkwareTypePO bo);

    String getMaxTypeCode();

    String getMaxModelCode(@Param("typeCode") String typeCode);

    Integer getCountByType(MWorkwareTypeModelPO so);
    /**
    * 增加工属具型号
     * */
    @Edit
    void insertBWorkwareTypeModel(MWorkwareTypeModelPO so);

    /**
     * 修改工属具型号
     * */
    void updateBWorkwareType(MWorkwareTypePO bo);

    /**
     * 根据工属具类型id删除工属具型号
     * */
    void deleteBWorkwareTypeModelByTypeCode(@Param("typeCode") String typeCode);

    /**
     * 删除工属具型号
     * */
    void deleteBWorkwareTypeModel(@Param("id") Long id);

    /**
     * 删除工属具类型
     * */
    void deleteBWorkwareType(Long id);

}
