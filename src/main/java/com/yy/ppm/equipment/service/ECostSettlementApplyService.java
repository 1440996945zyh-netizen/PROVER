package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.ECostSettlementApplyDTO;
import com.yy.ppm.equipment.bean.dto.ECostSettlementApplySearchDTO;
import com.yy.ppm.equipment.bean.dto.EMaintInfoDTO;
import com.yy.ppm.equipment.bean.dto.EMaintInfoSearchDTO;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;

import java.util.List;

/**
 * 结算申请 Service
 *
 * @author fanxianjin
 */
public interface ECostSettlementApplyService {

    /**
     * 查询结算申请列表
     */
    Pages<ECostSettlementApplyDTO> getList(ECostSettlementApplySearchDTO searchDTO);

    /**
     * 根据ID查询
     */
    ECostSettlementApplyDTO getById(Long id);

    /**
     * 保存（新增）
     */
    void add(ECostSettlementApplyDTO dto);

    /**
     * 修改
     */
    void update(ECostSettlementApplyDTO dto);

    /**
     * 根据ID删除
     */
    void deleteById(Long id);

    /**
     * 批量删除
     */
    void deleteByIds(List<Long> ids);

    /**
     * 查询已验收工单
     */
    Pages<EMaintInfoDTO> getAcceptedWorkOrders(EMaintInfoSearchDTO searchDTO);

    /**
     * 结算申请提交审批
     */
    void submitSettlementApply(BpmProcessInstanceDTO dto);

    /**
 * 功能描述: 根据流程实例ID获取业务ID
 * @return : java.lang.Long
     */
    Long getBusinessDataIdByProcessInstanceId(String processInstanceId);

    /**
     * 更新审批拒绝状态
     */
    void updateRejectStatusByApplyId(Long applyId, String isApprovalReject);
}
