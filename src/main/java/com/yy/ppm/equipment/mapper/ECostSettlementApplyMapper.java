package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.ECostSettlementApplyDTO;
import com.yy.ppm.equipment.bean.dto.ECostSettlementApplySearchDTO;
import com.yy.ppm.equipment.bean.dto.EMaintInfoDTO;
import com.yy.ppm.equipment.bean.dto.EMaintInfoSearchDTO;
import com.yy.ppm.equipment.bean.po.ECostSettlementApplyPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 结算申请 Mapper
 *
 * @author fanxianjin
 */
public interface ECostSettlementApplyMapper {

    /**
     * 查询结算申请列表
     */
    Page<ECostSettlementApplyDTO> selectList(ECostSettlementApplySearchDTO searchDTO);

    /**
     * 根据ID查询
     */
    ECostSettlementApplyDTO selectById(@Param("id") Long id);

    /**
     * 新增
     */
    @Edit
    void insert(ECostSettlementApplyPO po);

    /**
     * 修改
     */
    @Edit
    void update(ECostSettlementApplyPO po);

    /**
     * 删除
     */
    @Edit
    void deleteById(@Param("id") Long id);

    /**
     * 批量删除
     */
    @Edit
    void deleteByIds(@Param("ids") List<Long> ids);

    /**
     * 查询已验收且未结算的工单
     */
    Page<EMaintInfoDTO> selectAcceptedWorkOrders(EMaintInfoSearchDTO searchDTO);

    /**
     * 功能描述: 根据流程实例ID获取业务ID
     * @param processInstanceId
     * @return : java.lang.Long
     */
    Long getBusinessDataIdByProcessInstanceId(String processInstanceId);

}
