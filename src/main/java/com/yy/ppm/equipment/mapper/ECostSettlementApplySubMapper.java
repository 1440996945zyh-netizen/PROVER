package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.ECostSettlementApplySubDTO;
import com.yy.ppm.equipment.bean.po.ECostSettlementApplySubPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 结算申请子表 Mapper
 *
 * @author fanxianjin
 */
public interface ECostSettlementApplySubMapper {

    /**
     * 根据主表ID查询
     */
    List<ECostSettlementApplySubDTO> selectByApplyId(@Param("applyId") Long applyId);

    /**
     * 新增
     */
    @Edit
    void insert(ECostSettlementApplySubPO po);

    /**
     * 根据主表ID删除
     */
    @Edit
    void deleteByApplyId(@Param("applyId") Long applyId);

    /**
     * 更新审批拒绝状态
     */
    @Edit
    void updateRejectStatusByApplyId(@Param("applyId") Long applyId, @Param("isApprovalReject") String isApprovalReject);
}
