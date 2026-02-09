package com.yy.ppm.equipment.mapper;

import com.yy.ppm.equipment.bean.po.EMaterialStockCheckDetailPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物资库存盘点明细Mapper接口
 * @author system
 */
public interface EMaterialStockCheckDetailMapper {

    /**
     * 批量新增盘点明细
     */
    int batchInsertDetail(List<EMaterialStockCheckDetailPO> detailList);

    /**
     * 批量更新盘点明细
     */
    int batchUpdateDetail(List<EMaterialStockCheckDetailPO> detailList);

    /**
     * 删除盘点明细（根据盘点单ID）
     */
    int deleteDetailByCheckId(@Param("checkId") Long checkId);
}

