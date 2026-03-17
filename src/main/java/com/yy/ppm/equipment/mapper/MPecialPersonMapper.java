package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.MPecialPersonDTO;
import com.yy.ppm.equipment.bean.dto.MPecialPersonSearchDTO;
import com.yy.ppm.equipment.bean.po.MPecialPersonPO;
import org.apache.ibatis.annotations.Param;

/**
 * 特种作业人员证书Mapper接口
 * @author system
 */
public interface MPecialPersonMapper {

    /**
     * 查询特种作业人员证书列表（分页）
     */
    Page<MPecialPersonDTO> selectList(MPecialPersonSearchDTO searchDTO);

    /**
     * 根据ID查询特种作业人员证书
     */
    MPecialPersonDTO selectById(@Param("id") Long id);

    /**
     * 新增特种作业人员证书
     */
    @Edit
    void insert(MPecialPersonPO po);

    /**
     * 修改特种作业人员证书
     */
    @Edit
    void update(MPecialPersonPO po);

    /**
     * 删除特种作业人员证书（逻辑删除）
     */
    @Edit
    void deleteById(MPecialPersonPO po);
}

