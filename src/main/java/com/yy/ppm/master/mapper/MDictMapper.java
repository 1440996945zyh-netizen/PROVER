package com.yy.ppm.master.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MDictDataDTO;
import com.yy.ppm.master.bean.dto.MDictTypeDTO;
import com.yy.ppm.master.bean.dto.MDictTypeSearchDTO;
import com.yy.ppm.master.bean.po.MDictDataPO;
import com.yy.ppm.master.bean.po.MDictTypePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 字典及字典类型操作mapper
 *
 * @author 孙琦
 * @date 2023-4-26 16:57:35
 */
public interface MDictMapper {

    /**
     * 查询全部字典类型
     */
    Page<MDictTypeDTO> getDictTypeList(MDictTypeSearchDTO mDictTypeSearchDTO);


    /**
     * 查询全部字典类型
     */
    Page<MDictDataDTO> getDictList(MDictDataDTO po);



    /**
     * 新增字典类型
     */
    @Edit
    void insertDictType(MDictTypeDTO po);

    /**
     * 修改字典类型
     */
    @Edit
    Integer updateDictType(MDictTypeDTO po);

    /**
     * 查询该字典类型下是否有字典存在
     */
    Integer getCountDictByType(@Param("id") Long id);

    /**
     * 新增字典类型
     */
    Integer deleteDictTypeById(@Param("id") Long id);

    /**
     * 新增字典
     */
    @Edit
    void insertDict(MDictDataDTO po);

    /**
     * 根据字典类型获取全部字典值
     */
    Page<MDictDataDTO> getDictListByType(String typeCd);

    List<MDictDataDTO> getDictsByType(String typeCd);

    /**
     * 根据字典id查询字典
     */
    MDictTypeDTO getDictTypeById(Long id);

    /**
     * 修改字典
     */
    @Edit
    Integer updateDict(MDictDataDTO po);

    /**
     * 删除字典
     */
    Integer deleteDictById(@Param("id") Long id);


    /**
     * 根据id查询字典
     * @param id
     * @return
     */
    MDictDataDTO getDictById(Long id);
}
