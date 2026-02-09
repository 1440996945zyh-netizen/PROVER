package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.*;

/**
 * 物资申报Service接口
 * @author system
 */
public interface EMaterialApplicationService {

    /**
     * 查询物资申报列表（分页）
     */
    Pages<EMaterialApplicationDTO> getList(EMaterialApplicationSearchDTO searchDTO);

    /**
     * 根据ID查询物资申报（包含明细）
     */
    EMaterialApplicationDTO getById(Long id);

    /**
     * 新增或修改物资申报
     */
    void save(EMaterialApplicationDTO dto);

    /**
     * 删除物资申报
     */
    void deleteById(Long id);

    /**
     * 审批物资申报
     * @param id 申报ID
     * @param status 审批状态（3-审批通过，4-驳回）
     * @param approvalRemark 审批备注
     */
    void approve(Long id, String status, String approvalRemark);

    /**
     * 查询申报物资明细列表（用于采购时选择，关联物资申报表，只查询已审批通过的）
     */
    Pages<EMaterialApplicationDetailDTO> getDetailListForPurchase(EMaterialApplicationDetailSearchDTO searchDTO);

    /**
     * 查询物资申报明细关联采购明细列表（用于入库时选择，关联物资申报表、采购明细表、采购主表）
     */
    Pages<EMaterialApplicationDetailForWarehouseInDTO> getDetailListForWarehouseIn(EMaterialApplicationDetailForWarehouseInSearchDTO searchDTO);

    /**
     * 查询物资申报主表列表（包含明细列表，用于出库申请时选择）
     */
    Pages<EMaterialApplicationDTO> getListWithDetails(EMaterialApplicationSearchDTO searchDTO);
}

