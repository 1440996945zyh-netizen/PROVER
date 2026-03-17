package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.*;
import com.yy.ppm.equipment.bean.po.EMaterialStockCheckDetailPO;
import com.yy.ppm.equipment.bean.po.EMaterialStockCheckPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物资库存盘点Mapper接口
 * @author system
 */
public interface EMaterialStockCheckMapper {

    /**
     * 查询盘点单列表（分页）
     */
    Page<EMaterialStockCheckDTO> selectCheckList(EMaterialStockCheckSearchDTO searchDTO);

    /**
     * 根据ID查询盘点单
     */
    EMaterialStockCheckDTO selectById(@Param("id") Long id);

    /**
     * 新增盘点单
     */
    @Edit
    int insert(EMaterialStockCheckPO po);

    /**
     * 更新盘点单
     */
    @Edit
    int update(EMaterialStockCheckPO po);

    /**
     * 删除盘点单（逻辑删除）
     */
    int deleteById(@Param("id") Long id);

    /**
     * 检查盘点单号是否重复
     */
    int countByCheckNo(@Param("checkNo") String checkNo, @Param("id") Long id);

    /**
     * 查询盘点明细列表（根据盘点单ID）
     */
    List<EMaterialStockCheckDetailDTO> selectDetailListByCheckId(@Param("checkId") Long checkId);

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

    /**
     * 根据仓库ID查询所有入库明细并插入到盘点明细表（全量盘点，库存大于0的）
     * 直接在SQL中查询并插入
     */
    int insertDetailFromInDetailsByWarehouse(@Param("checkId") Long checkId, @Param("warehouseId") Long warehouseId);

    /**
     * 根据仓库ID和物资ID列表查询入库明细并插入到盘点明细表（部分盘点，库存大于0的）
     * 直接在SQL中查询并插入
     */
    int insertDetailFromInDetailsByWarehouseAndMaterials(@Param("checkId") Long checkId, @Param("warehouseId") Long warehouseId, @Param("materialIds") List<Long> materialIds);

    /**
     * 查询盘点明细列表（包含入库明细和入库主表信息）- 分页
     */
    Page<EMaterialStockCheckDetailWithInDTO> selectDetailListWithInInfo(EMaterialStockCheckDetailSearchDTO searchDTO);

    /**
     * 根据ID查询盘点明细
     */
    EMaterialStockCheckDetailDTO selectDetailById(@Param("id") Long id);

    /**
     * 更新盘点明细
     */
    @Edit
    int updateDetail(EMaterialStockCheckDetailPO po);
}

