package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaterialPurchaseDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialPurchaseSearchDTO;
import org.apache.ibatis.annotations.Param;

/**
 * 物资采购Mapper接口
 * @author system
 */
public interface EMaterialPurchaseMapper {

    /**
     * 查询物资采购列表（分页）
     */
    Page<EMaterialPurchaseDTO> selectList(EMaterialPurchaseSearchDTO searchDTO);

    /**
     * 根据ID查询物资采购
     */
    EMaterialPurchaseDTO selectById(@Param("id") Long id);

    /**
     * 新增物资采购
     */
    @Edit
    int insert(com.yy.ppm.equipment.bean.po.EMaterialPurchasePO po);

    /**
     * 修改物资采购
     */
    @Edit
    int update(com.yy.ppm.equipment.bean.po.EMaterialPurchasePO po);

    /**
     * 删除物资采购
     */
    int deleteById(@Param("id") Long id);

    /**
     * 检查采购单号是否重复
     */
    int countByPurchaseNo(@Param("purchaseNo") String purchaseNo, @Param("id") Long id);
}

