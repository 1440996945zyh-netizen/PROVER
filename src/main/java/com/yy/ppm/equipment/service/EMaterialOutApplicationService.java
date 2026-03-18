package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialOutApplicationDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialOutApplicationSearchDTO;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;

/**
 * 物资出库申请Service接口
 * @author system
 */
public interface EMaterialOutApplicationService {

    /**
     * 查询物资出库申请列表（分页）
     */
    Pages<EMaterialOutApplicationDTO> getList(EMaterialOutApplicationSearchDTO searchDTO);

    /**
     * 根据ID查询物资出库申请（包含明细）
     */
    EMaterialOutApplicationDTO getById(Long id);

    /**
     * 新增或修改物资出库申请
     */
    void save(EMaterialOutApplicationDTO dto);

    /**
     * 删除物资出库申请
     */
    void deleteById(Long id);

    /**
     * 审核物资出库申请
     * @param id 出库申请ID
     * @param status 审核状态（3-审批通过，4-驳回）
     * @param auditRemark 审核备注
     */
    void audit(Long id, String status, String auditRemark);

    /**
     * 查询物资出库申请列表（包含明细列表和库存数量，用于出库时选择）
     * 只查询审核通过的申请（状态为'3'）
     */
    Pages<EMaterialOutApplicationDTO> getListWithDetails(EMaterialOutApplicationSearchDTO searchDTO);


    void submit(BpmProcessInstanceDTO dto);
}

