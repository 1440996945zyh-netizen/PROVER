package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.*;

import java.util.List;

/**
 * 物资库存盘点Service接口
 * @author system
 */
public interface EMaterialStockCheckService {

    /**
     * 创建盘点单（根据仓库ID，自动加载该仓库所有物资的账面数量）
     */
    EMaterialStockCheckDTO createCheck(Long warehouseId, java.util.Date checkDate, String remark);

    /**
     * 创建盘点单（根据仓库ID和物资ID，只创建单个物资的盘点明细）
     * @param checkQuantity 盘点数量（可选，如果提供则在创建时直接设置盘点数量和差异）
     */
    EMaterialStockCheckDTO createCheckForMaterial(Long warehouseId, Long materialId, java.util.Date checkDate, String remark, java.math.BigDecimal checkQuantity);

    /**
     * 查询盘点单列表（分页）
     */
    Pages<EMaterialStockCheckDTO> getCheckList(EMaterialStockCheckSearchDTO searchDTO);

    /**
     * 根据ID查询盘点单（包含明细）
     */
    EMaterialStockCheckDTO getCheckById(Long id);

    /**
     * 查询盘点明细列表（根据盘点单ID）
     */
    List<EMaterialStockCheckDetailDTO> getCheckDetailList(Long checkId);

    /**
     * 更新盘点数量（单条明细）
     */
    void updateCheckQuantity(Long checkId, List<EMaterialStockCheckDetailDTO> detailList);

    /**
     * 完成盘点（计算差异）
     */
    void completeCheck(Long checkId);

    /**
     * 盘点调整（更新库存）
     */
    void adjustCheck(Long checkId, String remark);

    /**
     * 删除盘点单（只有待盘点状态才能删除）
     */
    void deleteCheck(Long id);

    /**
     * 快速盘点（创建盘点单、保存盘点数量、完成盘点并生成出入库单）
     * @param warehouseId 仓库ID
     * @param materialId 物资ID
     * @param checkDate 盘点日期
     * @param checkQuantity 盘点数量
     * @param remark 备注
     * @return 盘点单信息
     */
    EMaterialStockCheckDTO quickCheck(Long warehouseId, Long materialId, java.util.Date checkDate, java.math.BigDecimal checkQuantity, String remark);

    /**
     * 创建盘点单（根据仓库ID，查询该仓库所有入库明细，不合并）
     * @param warehouseId 仓库ID
     * @param checkStartDate 盘点开始日期
     * @param checkEndDate 盘点结束日期
     * @param checkTitle 盘点主题
     * @param remark 备注
     * @return 盘点单信息
     */
    EMaterialStockCheckDTO createCheckByWarehouse(Long warehouseId, java.util.Date checkStartDate, java.util.Date checkEndDate, String checkTitle, String remark);

    /**
     * 保存盘点单（新增或修改）
     * @param dto 盘点单DTO
     */
    void saveCheck(EMaterialStockCheckDTO dto);

    /**
     * 根据仓库ID查询所有入库明细（用于盘点，不合并）
     * @param warehouseId 仓库ID
     * @return 入库明细列表
     */
    List<EMaterialWarehouseInDetailDTO> getInDetailsByWarehouseId(Long warehouseId);

    /**
     * 查询盘点明细列表（包含入库明细和入库主表信息）- 分页
     * @param searchDTO 查询条件
     * @return 盘点明细列表（包含入库信息）
     */
   Pages<EMaterialStockCheckDetailWithInDTO> getCheckDetailListWithInInfo(EMaterialStockCheckDetailSearchDTO searchDTO);

    /**
     * 撤销盘点明细（状态回退，清空账面数量和盘点数量）
     * @param checkId 盘点单ID
     * @param detailId 明细ID
     */
    void cancelCheckDetail(Long checkId, Long detailId);

    /**
     * 审核盘点明细（单个明细审核）
     * @param checkId 盘点单ID
     * @param detailId 明细ID
     */
    void auditCheckDetail(Long checkId, Long detailId);

    /**
     * 无差异操作（批量）
     * @param checkId 盘点单ID
     * @param detailList 明细列表
     */
    void noDifference(Long checkId, List<EMaterialStockCheckDetailDTO> detailList);
}

