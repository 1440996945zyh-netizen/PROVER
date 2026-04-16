package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialAllocateDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialAllocateSearchDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockSearchDTO;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;

/**
 * 物资调拨Service
 * @author system
 */
public interface EMaterialAllocateService {

    /**
     * 查询物资调拨列表
     * @param searchDTO 查询条件
     * @return 分页结果
     */
    Pages<EMaterialAllocateDTO> getList(EMaterialAllocateSearchDTO searchDTO);

    /**
     * 根据ID查询物资调拨详情
     * @param id 主键ID
     * @return 调拨详情
     */
    EMaterialAllocateDTO getById(Long id);

    /**
     * 新增或修改物资调拨单
     * @param dto 调拨数据
     */
    void save(EMaterialAllocateDTO dto);

    /**
     * 删除物资调拨单
     * @param id 主键ID
     */
    void deleteById(Long id);

    /**
     * 提交物资调拨审批
     * @param dto 流程提交参数
     */
    void submitMaterialAllocate(BpmProcessInstanceDTO dto);

    /**
     * 根据流程实例ID获取业务主键
     * @param processInstanceId 流程实例ID
     * @return 业务主键
     */
    Long getBusinessDataIdByProcessInstanceId(String processInstanceId);

    /**
     * 审批通过后执行调拨
     * @param id 调拨单ID
     */
    void executeAllocate(Long id);

    /**
     * 回写调拨执行结果
     * @param id 调拨单ID
     * @param executeStatus 执行状态
     * @param executeMsg 执行结果说明
     * @param outWarehouseId 调出库单ID
     * @param outWarehouseNo 调出库单号
     * @param inWarehouseId 调入库单ID
     * @param inWarehouseNo 调入库单号
     */
    void updateExecuteResult(Long id, Integer executeStatus, String executeMsg,
                             Long outWarehouseId, String outWarehouseNo,
                             Long inWarehouseId, String inWarehouseNo);

    /**
     * 查询待调拨物资列表
     * @param searchDTO 查询条件
     * @return 分页结果
     */
    Pages<EMaterialStockDTO> selectMaterial(EMaterialStockSearchDTO searchDTO);
}
