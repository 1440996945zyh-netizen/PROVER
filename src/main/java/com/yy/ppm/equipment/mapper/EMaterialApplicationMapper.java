package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaterialApplicationDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialApplicationSearchDTO;
import org.apache.ibatis.annotations.Param;

/**
 * 物资申报Mapper接口
 * @author system
 */
public interface EMaterialApplicationMapper {

    /**
     * 查询物资申报列表（分页）
     */
    Page<EMaterialApplicationDTO> selectList(EMaterialApplicationSearchDTO searchDTO);

    /**
     * 查询物资申报列表（包含明细和库存数量，分页）
     */
    Page<EMaterialApplicationDTO> selectListWithDetails(EMaterialApplicationSearchDTO searchDTO);

    /**
     * 根据ID查询物资申报
     */
    EMaterialApplicationDTO selectById(@Param("id") Long id);

    /**
     * 新增物资申报
     */
   @Edit
    int insert(com.yy.ppm.equipment.bean.po.EMaterialApplicationPO po);

    /**
     * 修改物资申报
     */
    @Edit
    int update(com.yy.ppm.equipment.bean.po.EMaterialApplicationPO po);

    /**
     * 删除物资申报
     */
    int deleteById(@Param("id") Long id);

    /**
     * 检查申请单号是否重复
     */
    int countByApplicationNo(@Param("applicationNo") String applicationNo, @Param("id") Long id);

    /**
     * 审批物资申报
     */
    @Edit
    int approve(com.yy.ppm.equipment.bean.po.EMaterialApplicationPO po);
}

