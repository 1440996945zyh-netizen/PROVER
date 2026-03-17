package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.SerialNumberPrefixEnum;
import com.yy.ppm.common.service.impl.CommonServiceImpl;
import com.yy.ppm.equipment.bean.dto.ECostSettlementApplyDTO;
import com.yy.ppm.equipment.bean.dto.ECostSettlementApplySearchDTO;
import com.yy.ppm.equipment.bean.dto.ECostSettlementApplySubDTO;
import com.yy.ppm.equipment.bean.dto.EMaintInfoDTO;
import com.yy.ppm.equipment.bean.dto.EMaintInfoSearchDTO;
import com.yy.ppm.equipment.bean.po.ECostSettlementApplyPO;
import com.yy.ppm.equipment.bean.po.ECostSettlementApplySubPO;
import com.yy.ppm.equipment.mapper.ECostSettlementApplyMapper;
import com.yy.ppm.equipment.mapper.ECostSettlementApplySubMapper;
import com.yy.ppm.equipment.service.ECostSettlementApplyService;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;
import com.yy.ppm.flowable.service.BpmProcessInstanceService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.yy.common.util.SecurityUtils.getLoginUserId;

/**
 * 结算申请 Service 实现类
 *
 * @author fanxianjin
 */
@RequiredArgsConstructor
@Service
public class ECostSettlementApplyServiceImpl implements ECostSettlementApplyService {

    @Resource
    private ECostSettlementApplyMapper mapper;

    @Resource
    private ECostSettlementApplySubMapper subMapper;

    @Resource
    private Snowflake snowflake;

    @Resource
    private CommonServiceImpl commonService;

    @Resource
    BpmProcessInstanceService bpmProcessInstanceService;

    @Override
    public Pages<ECostSettlementApplyDTO> getList(ECostSettlementApplySearchDTO searchDTO) {
        return PageHelperUtils.limit(searchDTO, () -> mapper.selectList(searchDTO));
    }

    @Override
    public ECostSettlementApplyDTO getById(Long id) {
        ECostSettlementApplyDTO dto = mapper.selectById(id);
        if (dto != null) {
            dto.setSubList(subMapper.selectByApplyId(id));
        }
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void add(ECostSettlementApplyDTO dto) {
        if (dto.getMaintOrgId() == null) {
            throw new BusinessRuntimeException("维修单位不能为空");
        }
        if (dto.getProjectType() == null || dto.getProjectType().isEmpty()) {
            throw new BusinessRuntimeException("项目类型不能为空");
        }
        if (dto.getSubList() == null || dto.getSubList().isEmpty()) {
            throw new BusinessRuntimeException("请选择至少一个工单进行结算");
        }

        ECostSettlementApplyPO po = new ECostSettlementApplyPO();
        BeanUtils.copyProperties(dto, po);

        // 计算总金额
        calculateAmounts(dto, po);

        Date now = new Date();
        po.setNow(now);
        po.setLoginUserId(SecurityUtils.getLoginUserId());
        po.setLoginUserName(SecurityUtils.getLoginUserName());

        // 新增时后端生成结算单号
        po.setId(snowflake.nextId());
        po.setSettlementNo(commonService.generateSerialNumber(SerialNumberPrefixEnum.SETTLEMENT));
        po.setApplyUserId(SecurityUtils.getLoginUserId());
        po.setApplyUserName(SecurityUtils.getLoginUserName());
        po.setApplyTime(now);
        mapper.insert(po);

        // 插入子表
        saveSubList(po.getId(), dto.getSubList(), now);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void update(ECostSettlementApplyDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }
        // 判断当前状态，仅未发起状态下可修改
        ECostSettlementApplyDTO settlementApplyDTO = mapper.selectById(dto.getId());
        if (settlementApplyDTO == null) {
            throw new BusinessRuntimeException("业务数据不存在");
        }
        if (!"0".equals(settlementApplyDTO.getProcessStatus())) {
            throw new BusinessRuntimeException("仅未发起状态下可修改");
        }

        ECostSettlementApplyPO po = new ECostSettlementApplyPO();
        BeanUtils.copyProperties(dto, po);

        // 计算总金额
        calculateAmounts(dto, po);

        Date now = new Date();
        po.setNow(now);
        po.setLoginUserId(SecurityUtils.getLoginUserId());
        po.setLoginUserName(SecurityUtils.getLoginUserName());

        mapper.update(po);

        // 修改时先删除旧子表再插入新子表
        subMapper.deleteByApplyId(dto.getId());
        saveSubList(po.getId(), dto.getSubList(), now);
    }

    private void calculateAmounts(ECostSettlementApplyDTO dto, ECostSettlementApplyPO po) {
        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;
        for (ECostSettlementApplySubDTO sub : dto.getSubList()) {
            if (sub.getBudgetAmount() != null) {
                totalBudget = totalBudget.add(sub.getBudgetAmount());
            }
            if (sub.getActualAmount() != null) {
                totalActual = totalActual.add(sub.getActualAmount());
            } else {
                // 实际金额（可编辑默认和预算金额一致）
                sub.setActualAmount(sub.getBudgetAmount());
                totalActual = totalActual.add(sub.getBudgetAmount() != null ? sub.getBudgetAmount() : BigDecimal.ZERO);
            }
        }
        po.setTotalBudgetAmount(totalBudget);
        po.setTotalActualAmount(totalActual);
    }

    private void saveSubList(Long applyId, List<ECostSettlementApplySubDTO> subList, Date now) {
        if (subList != null) {
            for (ECostSettlementApplySubDTO subDto : subList) {
                ECostSettlementApplySubPO subPo = new ECostSettlementApplySubPO();
                BeanUtils.copyProperties(subDto, subPo);
                subPo.setId(snowflake.nextId());
                subPo.setApplyId(applyId);
                subPo.setNow(now);
                subPo.setLoginUserId(SecurityUtils.getLoginUserId());
                subPo.setLoginUserName(SecurityUtils.getLoginUserName());
                subMapper.insert(subPo);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteById(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }
        // 判断当前状态，仅已发起状态下可删除
        ECostSettlementApplyDTO settlementApplyDTO = mapper.selectById(id);
        if (settlementApplyDTO == null) {
            throw new BusinessRuntimeException("业务数据不存在");
        }
        if (!"0".equals(settlementApplyDTO.getProcessStatus())) {
            throw new BusinessRuntimeException("仅未发起状态下可删除");
        }
        subMapper.deleteByApplyId(id);
        mapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessRuntimeException("IDs不能为空");
        }
        for (Long id : ids) {
            subMapper.deleteByApplyId(id);
        }
        mapper.deleteByIds(ids);
    }

    @Override
    public Pages<EMaintInfoDTO> getAcceptedWorkOrders(EMaintInfoSearchDTO searchDTO) {
        if (searchDTO.getMaintOrgId() == null) {
            throw new BusinessRuntimeException("维修单位不能为空");
        }
        if (searchDTO.getProjectType() == null) {
            throw new BusinessRuntimeException("项目类型不能为空");
        }
        return PageHelperUtils.limit(searchDTO, () -> mapper.selectAcceptedWorkOrders(searchDTO));
    }

    @Override
    public void submitSettlementApply(BpmProcessInstanceDTO dto) {
        // 判断当前状态，仅未发起状态下可提交
        if (dto.getBusinessDataId() == null) {
            throw new BusinessRuntimeException("业务数据ID不能为空");
        }
        ECostSettlementApplyDTO settlementApplyDTO = mapper.selectById(dto.getBusinessDataId());
        if (settlementApplyDTO == null) {
            throw new BusinessRuntimeException("业务数据不存在");
        }
        if (!"0".equals(settlementApplyDTO.getProcessStatus())) {
            throw new BusinessRuntimeException("仅未发起状态下可提交");
        }
        // 调用流程实例发起
        bpmProcessInstanceService.createProcessInstance(getLoginUserId(), dto);
    }

    @Override
    public Long getBusinessDataIdByProcessInstanceId(String processInstanceId) {
        return mapper.getBusinessDataIdByProcessInstanceId(processInstanceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRejectStatusByApplyId(Long applyId, String isApprovalReject) {
        subMapper.updateRejectStatusByApplyId(applyId, isApprovalReject);
    }
}
