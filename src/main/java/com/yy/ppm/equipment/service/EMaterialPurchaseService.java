package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialPurchaseDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialPurchaseSearchDTO;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;

/**
 * 物资采购Service接口
 * @author system
 */
public interface EMaterialPurchaseService {

    /**
     * 查询物资采购列表（分页）
     */
    Pages<EMaterialPurchaseDTO> getList(EMaterialPurchaseSearchDTO searchDTO);

    /**
     * 根据ID查询物资采购（包含明细）
     */
    EMaterialPurchaseDTO getById(Long id);

    /**
     * 新增或修改物资采购
     */
    void save(EMaterialPurchaseDTO dto);

    /**
     * 删除物资采购
     */
    void deleteById(Long id);

    /**
     * 标记采购失败
     */
    void markAsFailed(Long id, String failureReason);

    /**
     * 审核物资采购
     * @param id 采购单ID
     * @param status 审核状态（1-审核通过，2-驳回）
     * @param approvalRemark 审核备注（暂不使用，数据库无此字段）
     */
    void approve(Long id, Integer status, String approvalRemark);


    void submit(BpmProcessInstanceDTO dto);
}

