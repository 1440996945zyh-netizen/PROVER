package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.alibaba.fastjson2.JSON;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.UUIDUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.EMaterialAllocateDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialAllocateDetailDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialAllocateSearchDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockSearchDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseInAcceptanceDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseInDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseInDetailDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseOutDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseOutDetailDTO;
import com.yy.ppm.equipment.bean.po.EMaterialAllocateDetailPO;
import com.yy.ppm.equipment.bean.po.EMaterialAllocateHistoryPO;
import com.yy.ppm.equipment.bean.po.EMaterialAllocatePO;
import com.yy.ppm.equipment.mapper.EMaterialAllocateDetailMapper;
import com.yy.ppm.equipment.mapper.EMaterialAllocateHistoryMapper;
import com.yy.ppm.equipment.mapper.EMaterialAllocateMapper;
import com.yy.ppm.equipment.mapper.EMaterialStockMapper;
import com.yy.ppm.equipment.mapper.EMaterialWarehouseInOutRelMapper;
import com.yy.ppm.equipment.service.EMaterialAllocateService;
import com.yy.ppm.equipment.service.EMaterialWarehouseInService;
import com.yy.ppm.equipment.service.EMaterialWarehouseOutService;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;
import com.yy.ppm.flowable.service.BpmProcessInstanceService;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.yy.common.util.SecurityUtils.getLoginUserId;
import com.yy.ppm.auth.bean.dto.UserAuthorizeInfo;
import com.yy.ppm.auth.bean.dto.UserInfo;

/**
 * 物资调拨ServiceImpl
 * @author system
 */
@Service
public class EMaterialAllocateServiceImpl implements EMaterialAllocateService {

    private static final MicroLogger LOGGER = new MicroLogger(EMaterialAllocateServiceImpl.class);

    @Resource
    private EMaterialAllocateMapper allocateMapper;

    @Resource
    private EMaterialAllocateDetailMapper allocateDetailMapper;

    @Resource
    private EMaterialAllocateHistoryMapper allocateHistoryMapper;

    @Resource
    private EMaterialWarehouseOutService materialWarehouseOutService;

    @Resource
    private EMaterialWarehouseInService materialWarehouseInService;

    @Resource
    private EMaterialStockMapper materialStockMapper;

    @Resource
    private EMaterialWarehouseInOutRelMapper materialWarehouseInOutRelMapper;

    @Resource
    private BpmProcessInstanceService bpmProcessInstanceService;

    @Resource
    private Snowflake snowflake;

    @Resource
    private SecurityUtils securityUtils;

    /**
     * 查询物资调拨列表
     */
    @Override
    public Pages<EMaterialAllocateDTO> getList(EMaterialAllocateSearchDTO searchDTO) {
        return PageHelperUtils.limit(searchDTO, () -> allocateMapper.getList(searchDTO));
    }

    /**
     * 查询物资调拨详情
     */
    @Override
    public EMaterialAllocateDTO getById(Long id) {
        EMaterialAllocateDTO dto = allocateMapper.getById(id);
        if (dto != null) {
            dto.setDetailList(allocateDetailMapper.selectListByAllocateId(id));
        }
        return dto;
    }

    /**
     * 新增或修改调拨单。
     * 新增时只保存业务数据，不发起审批、不动库存；
     * 修改时仅允许未发起审批且未执行成功的单据进行编辑。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(EMaterialAllocateDTO dto) {
        final String methodName = "EMaterialAllocateServiceImpl:save";
        LOGGER.enter(methodName, "dto:" + dto);
        // 先做基础业务校验，避免落库后再回滚
        validateAllocate(dto);

        Long loginUserId = SecurityUtils.getLoginUserId();
        String loginUserName = SecurityUtils.getLoginUserName();
        Date now = new Date();
        EMaterialAllocatePO po = new EMaterialAllocatePO();
        BeanUtils.copyProperties(dto, po);

        if (dto.getId() == null) {
            // 新增时生成主键和调拨编号
            Long id = snowflake.nextId();
            po.setId(id);
            dto.setId(id);
            String allocateCode = UUIDUtils.getDbOrderCode(loginUserId);
            po.setAllocateCode(allocateCode);
            dto.setAllocateCode(allocateCode);
            // 调拨时间由后端统一按保存时间落库，避免前端手工维护时间口径
            po.setAllocateTime(now);
            dto.setAllocateTime(now);
            po.setApplyUser(loginUserId);
            po.setApplyUserName(loginUserName);
            po.setExecuteStatus(0);
            po.setCreateBy(loginUserId);
            po.setCreateByName(loginUserName);
            po.setCreateTime(now);
            allocateMapper.insert(po);
        } else {
            // 修改前校验流程状态和执行状态，保证只有草稿单可编辑
            EMaterialAllocateDTO oldDto = getById(dto.getId());
            if (oldDto == null) {
                throw new BusinessRuntimeException("物资调拨单不存在");
            }
            if (!"0".equals(oldDto.getProcessStatus())) {
                throw new BusinessRuntimeException("已发起审批的调拨单不允许修改");
            }
            if (oldDto.getExecuteStatus() != null && oldDto.getExecuteStatus() == 1) {
                throw new BusinessRuntimeException("已执行成功的调拨单不允许修改");
            }
            // 编辑场景保留原调拨时间，前端不再维护这个字段
            po.setAllocateTime(oldDto.getAllocateTime());
            po.setUpdateBy(loginUserId);
            po.setUpdateByName(loginUserName);
            po.setUpdateTime(now);
            allocateMapper.update(po);
            // 采用“删明细后重建”的方式保持前后端传参与库存逻辑一致
            allocateDetailMapper.deleteByAllocateId(dto.getId());
        }

        // 明细统一重建，保持排序号和显示顺序一致
        saveDetails(dto.getId(), dto.getDetailList(), loginUserId, loginUserName, now);
        LOGGER.exit(methodName, "");
    }

    /**
     * 删除调拨单。
     * 只允许删除未发起审批且未执行成功的单据。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        EMaterialAllocateDTO dto = getById(id);
        if (dto == null) {
            return;
        }
        if (!"0".equals(dto.getProcessStatus())) {
            throw new BusinessRuntimeException("只有未发起的调拨单才允许删除");
        }
        if (dto.getExecuteStatus() != null && dto.getExecuteStatus() == 1) {
            throw new BusinessRuntimeException("已执行成功的调拨单不允许删除");
        }
        allocateHistoryMapper.deleteByAllocateId(id);
        allocateDetailMapper.deleteByAllocateId(id);
        allocateMapper.deleteById(id);
    }

    /**
     * 提交审批。
     * 这里不做库存动作，只负责校验单据状态并发起流程实例。
     */
    @Override
    public void submitMaterialAllocate(BpmProcessInstanceDTO dto) {
        if (dto.getBusinessDataId() == null) {
            throw new BusinessRuntimeException("业务数据ID不能为空");
        }
        EMaterialAllocateDTO allocateDTO = getById(dto.getBusinessDataId());
        if (allocateDTO == null) {
            throw new BusinessRuntimeException("业务数据不存在");
        }
        if (!"0".equals(allocateDTO.getProcessStatus())) {
            throw new BusinessRuntimeException("仅未发起状态下可提交");
        }
        bpmProcessInstanceService.createProcessInstance(getLoginUserId(), dto);
    }

    /**
     * 根据流程实例ID查询业务主键
     */
    @Override
    public Long getBusinessDataIdByProcessInstanceId(String processInstanceId) {
        return allocateMapper.getBusinessDataIdByProcessInstanceId(processInstanceId);
    }

    /**
     * 执行调拨。
     * 业务顺序固定为：先调出，再调入，最后回写执行结果。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeAllocate(Long id) {
        final String methodName = "EMaterialAllocateServiceImpl:executeAllocate";
        LOGGER.enter(methodName, "id:" + id);
        EMaterialAllocateDTO allocateDTO = getById(id);
        if (allocateDTO == null) {
            throw new BusinessRuntimeException("物资调拨单不存在");
        }
        if (allocateDTO.getExecuteStatus() != null && allocateDTO.getExecuteStatus() == 1) {
            LOGGER.exit(methodName, "already executed");
            return;
        }
        if (CollectionUtils.isEmpty(allocateDTO.getDetailList())) {
            throw new BusinessRuntimeException("调拨明细不能为空");
        }

        // 执行前先留一份快照，方便审计和排错
        prepareHistory(allocateDTO);

        Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            // 流程监听器场景下可能没有登录态，这里临时构造申请人的认证信息给出入库服务复用
            setAuthentication(allocateDTO);

            // 1. 生成并确认调出库单，真正扣减源仓库存
            EMaterialWarehouseOutDTO outDTO = buildWarehouseOutDTO(allocateDTO);
            materialWarehouseOutService.save(outDTO);
            materialWarehouseOutService.confirm(outDTO.getId());
            EMaterialWarehouseOutDTO savedOutDTO = materialWarehouseOutService.getById(outDTO.getId());

            // 2. 以实际调出拆批结果为准生成调入明细，确保不同批次不同单价时调入仍然准确
            List<AllocateSplitDetail> splitDetailList = buildSplitDetailList(savedOutDTO.getId());
            EMaterialWarehouseInDTO inDTO = buildWarehouseInDTO(allocateDTO, splitDetailList);
            materialWarehouseInService.save(inDTO);
            EMaterialWarehouseInAcceptanceDTO acceptanceDTO = new EMaterialWarehouseInAcceptanceDTO();
            acceptanceDTO.setId(inDTO.getId());
            acceptanceDTO.setAcceptanceStatus(1);
            acceptanceDTO.setAcceptanceRemarks("调拨自动入库");
            materialWarehouseInService.acceptance(acceptanceDTO);
            EMaterialWarehouseInDTO savedInDTO = materialWarehouseInService.getById(inDTO.getId());

            // 3. 把自动生成的出入库明细ID回写到调拨明细，便于后续追溯
            updateDetailRelationIds(allocateDTO, savedOutDTO, savedInDTO, splitDetailList);

            // 4. 回写执行结果和关联单号
            updateExecuteResult(id, 1, "执行成功",
                    savedOutDTO.getId(), savedOutDTO.getWarehouseOutNo(),
                    savedInDTO.getId(), savedInDTO.getWarehouseInNo());
        } finally {
            // 恢复原认证信息，避免影响后续线程上下文
            SecurityContextHolder.getContext().setAuthentication(oldAuthentication);
        }
        LOGGER.exit(methodName, "");
    }

    /**
     * 回写执行状态。
     * 单独使用新事务，确保流程监听器失败时也能尽量保留失败原因。
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateExecuteResult(Long id, Integer executeStatus, String executeMsg,
                                    Long outWarehouseId, String outWarehouseNo,
                                    Long inWarehouseId, String inWarehouseNo) {
        EMaterialAllocatePO po = new EMaterialAllocatePO();
        po.setId(id);
        po.setExecuteStatus(executeStatus);
        po.setExecuteMsg(executeMsg);
        po.setOutWarehouseId(outWarehouseId);
        po.setOutWarehouseNo(outWarehouseNo);
        po.setInWarehouseId(inWarehouseId);
        po.setInWarehouseNo(inWarehouseNo);
        po.setExecuteTime(executeStatus != null && executeStatus == 1 ? new Date() : null);
        po.setExecuteUser(safeGetLoginUserId());
        po.setExecuteUserName(safeGetLoginUserName());
        po.setUpdateBy(safeGetLoginUserId());
        po.setUpdateByName(safeGetLoginUserName());
        po.setUpdateTime(new Date());
        allocateMapper.updateExecuteResult(po);
    }

    /**
     * 查询待调拨物资。
     * 直接复用现有库存汇总口径，确保“可调数量”和库存页保持一致。
     */
    @Override
    public Pages<EMaterialStockDTO> selectMaterial(EMaterialStockSearchDTO searchDTO) {
        if (searchDTO.getWarehouseId() == null) {
            throw new BusinessRuntimeException("请选择调出仓库");
        }
        return PageHelperUtils.limit(searchDTO, () -> materialStockMapper.selectStockList(searchDTO));
    }

    /**
     * 调拨单基础校验
     */
    private void validateAllocate(EMaterialAllocateDTO dto) {
        if (dto == null) {
            throw new BusinessRuntimeException("调拨数据不能为空");
        }
        if (dto.getFromCompanyId() == null) {
            throw new BusinessRuntimeException("调出单位不能为空");
        }
        if (dto.getFromWarehouseId() == null) {
            throw new BusinessRuntimeException("调出仓库不能为空");
        }
        if (dto.getToCompanyId() == null) {
            throw new BusinessRuntimeException("调入单位不能为空");
        }
        if (dto.getToWarehouseId() == null) {
            throw new BusinessRuntimeException("调入仓库不能为空");
        }
        if (dto.getFromWarehouseId().equals(dto.getToWarehouseId())) {
            throw new BusinessRuntimeException("调出仓库与调入仓库不能相同");
        }
        if (CollectionUtils.isEmpty(dto.getDetailList())) {
            throw new BusinessRuntimeException("调拨明细不能为空");
        }
        for (EMaterialAllocateDetailDTO detailDTO : dto.getDetailList()) {
            if (detailDTO.getMaterialId() == null) {
                throw new BusinessRuntimeException("物资不能为空");
            }
            if (detailDTO.getAllocQty() == null || detailDTO.getAllocQty().signum() <= 0) {
                throw new BusinessRuntimeException("调拨数量必须大于0");
            }
        }
    }

    /**
     * 保存调拨明细，并按前端顺序生成排序号
     */
    private void saveDetails(Long allocateId, List<EMaterialAllocateDetailDTO> detailList,
                             Long loginUserId, String loginUserName, Date now) {
        List<EMaterialAllocateDetailPO> poList = new ArrayList<>();
        int sortNum = 1;
        for (EMaterialAllocateDetailDTO detailDTO : detailList) {
            EMaterialAllocateDetailPO po = new EMaterialAllocateDetailPO();
            BeanUtils.copyProperties(detailDTO, po);
            po.setId(snowflake.nextId());
            po.setAllocateId(allocateId);
            po.setSortNum(sortNum++);
            po.setCreateBy(loginUserId);
            po.setCreateByName(loginUserName);
            po.setCreateTime(now);
            poList.add(po);
        }
        if (CollectionUtils.isNotEmpty(poList)) {
            allocateDetailMapper.batchInsert(poList);
        }
    }

    /**
     * 执行前生成历史快照
     */
    private void prepareHistory(EMaterialAllocateDTO allocateDTO) {
        allocateHistoryMapper.deleteByAllocateId(allocateDTO.getId());
        Long loginUserId = SecurityUtils.getLoginUserId();
        String loginUserName = SecurityUtils.getLoginUserName();
        Date now = new Date();
        for (EMaterialAllocateDetailDTO detailDTO : allocateDTO.getDetailList()) {
            EMaterialAllocateHistoryPO historyPO = new EMaterialAllocateHistoryPO();
            historyPO.setId(snowflake.nextId());
            historyPO.setAllocateId(allocateDTO.getId());
            historyPO.setAllocateDetailId(detailDTO.getId());
            historyPO.setMaterialId(detailDTO.getMaterialId());
            historyPO.setLastChangeInfo(JSON.toJSONString(detailDTO));
            historyPO.setRemark("调拨执行前快照");
            historyPO.setCreateBy(loginUserId);
            historyPO.setCreateByName(loginUserName);
            historyPO.setCreateTime(now);
            allocateHistoryMapper.insert(historyPO);
        }
    }

    /**
     * 组装调出库单
     */
    private EMaterialWarehouseOutDTO buildWarehouseOutDTO(EMaterialAllocateDTO allocateDTO) {
        EMaterialWarehouseOutDTO dto = new EMaterialWarehouseOutDTO();
        dto.setWarehouseOutTitle(allocateDTO.getTitle());
        dto.setWarehouseId(allocateDTO.getFromWarehouseId());
        dto.setWarehouseName(allocateDTO.getFromWarehouseName());
//        dto.setDeptId(allocateDTO.getFromCompanyId());
//        dto.setDeptName(allocateDTO.getFromCompanyName());
        UserInfo userInfo = securityUtils.getUserInfo();
//        dto.setReceiverId(allocateDTO.getApplyUser());
//        dto.setReceiverName(allocateDTO.getApplyUserName());
        dto.setStatus(0);
        dto.setWarehouseOutTypeCode("03");
        dto.setWarehouseOutTypeName("调拨出库");
        dto.setSourceBizId(allocateDTO.getId());
        dto.setSourceBizNo(allocateDTO.getAllocateCode());

        List<EMaterialWarehouseOutDetailDTO> detailList = new ArrayList<>();
        // 调出明细与调拨明细一一对应，真正FIFO拆批仍由现有出库服务处理
        for (EMaterialAllocateDetailDTO detailDTO : sortDetailList(allocateDTO.getDetailList())) {
            EMaterialWarehouseOutDetailDTO outDetailDTO = new EMaterialWarehouseOutDetailDTO();
            outDetailDTO.setMaterialId(detailDTO.getMaterialId());
            outDetailDTO.setMaterialCode(detailDTO.getMaterialCode());
            outDetailDTO.setMaterialName(detailDTO.getMaterialName());
            outDetailDTO.setSpecificationModel(detailDTO.getSpecification());
            outDetailDTO.setBrand(detailDTO.getBrand());
            outDetailDTO.setUnitCode(detailDTO.getUnitCode());
            outDetailDTO.setUnitName(detailDTO.getUnitName());
            outDetailDTO.setOutQuantity(detailDTO.getAllocQty());
            outDetailDTO.setFlowDirection(detailDTO.getFlowDirection());
            detailList.add(outDetailDTO);
        }
        dto.setDetailList(detailList);
        return dto;
    }

    /**
     * 组装调入库单
     */
    private EMaterialWarehouseInDTO buildWarehouseInDTO(EMaterialAllocateDTO allocateDTO,
                                                        List<AllocateSplitDetail> splitDetailList) {
        EMaterialWarehouseInDTO dto = new EMaterialWarehouseInDTO();
//        dto.setWarehouseInTitle("物资调拨入库-" + allocateDTO.getAllocateCode());
        dto.setWarehouseInTitle(allocateDTO.getTitle());
        dto.setWarehouseInTypeCode("07");
        dto.setWarehouseInTypeName("调拨入库");
        dto.setSupplierName("内部调拨");
        dto.setWarehouseInDate(new Date());
//        dto.setDeptId(allocateDTO.getToCompanyId());
//        dto.setDeptName(allocateDTO.getToCompanyName());
        dto.setWarehouseId(allocateDTO.getToWarehouseId());
        dto.setWarehouseName(allocateDTO.getToWarehouseName());
        dto.setRemarks("由物资调拨单自动生成");
        dto.setAcceptanceStatus(1);
        dto.setAcceptanceRemarks("调拨自动入库");
        dto.setSourceBizId(allocateDTO.getId());
        dto.setSourceBizNo(allocateDTO.getAllocateCode());

        List<EMaterialWarehouseInDetailDTO> detailList = new ArrayList<>();
        // 调入严格按实际调出拆批结果逐条生成，完整继承真实来源批次的单价和金额
        for (AllocateSplitDetail splitDetail : splitDetailList) {
            EMaterialWarehouseInDetailDTO inDetailDTO = new EMaterialWarehouseInDetailDTO();
            inDetailDTO.setMaterialId(splitDetail.getMaterialId());
            inDetailDTO.setMaterialCode(splitDetail.getMaterialCode());
            inDetailDTO.setMaterialName(splitDetail.getMaterialName());
            inDetailDTO.setSpecification(splitDetail.getSpecification());
            inDetailDTO.setSpecificationDesc(splitDetail.getSpecificationDesc());
            inDetailDTO.setBrand(splitDetail.getBrand());
            inDetailDTO.setUnit(splitDetail.getUnitName());
            inDetailDTO.setUnitCode(splitDetail.getUnitCode());
            inDetailDTO.setWarehouseInQuantity(splitDetail.getQuantity());
            inDetailDTO.setTaxIncludedUnitPrice(splitDetail.getTaxIncludedUnitPrice());
            inDetailDTO.setTaxIncludedAmount(splitDetail.getTaxIncludedAmount());
            detailList.add(inDetailDTO);
        }
        dto.setDetailList(detailList);
        return dto;
    }

    /**
     * 回写调拨明细关联的出入库明细ID
     */
    private void updateDetailRelationIds(EMaterialAllocateDTO allocateDTO,
                                         EMaterialWarehouseOutDTO outDTO,
                                         EMaterialWarehouseInDTO inDTO,
                                         List<AllocateSplitDetail> splitDetailList) {
        List<EMaterialAllocateDetailDTO> allocateDetails = sortDetailList(allocateDTO.getDetailList());
        List<EMaterialWarehouseOutDetailDTO> outDetails = outDTO == null || outDTO.getDetailList() == null
                ? new ArrayList<>() : outDTO.getDetailList();
        List<EMaterialWarehouseInDetailDTO> inDetails = inDTO == null || inDTO.getDetailList() == null
                ? new ArrayList<>() : inDTO.getDetailList();

        for (int i = 0; i < allocateDetails.size(); i++) {
            Long outDetailId = i < outDetails.size() ? outDetails.get(i).getId() : null;
            Long inDetailId = findFirstInDetailIdByOutDetailId(outDetailId, splitDetailList, inDetails);
            allocateDetailMapper.updateRelationIds(
                    allocateDetails.get(i).getId(),
                    outDetailId,
                    inDetailId,
                    safeGetLoginUserId(),
                    safeGetLoginUserName()
            );
        }
    }

    /**
     * 按调拨明细顺序排序，保证落单顺序稳定
     */
    private List<EMaterialAllocateDetailDTO> sortDetailList(List<EMaterialAllocateDetailDTO> detailList) {
        List<EMaterialAllocateDetailDTO> result = new ArrayList<>(detailList);
        result.sort(Comparator.comparing(EMaterialAllocateDetailDTO::getSortNum,
                Comparator.nullsLast(Integer::compareTo)));
        return result;
    }

    /**
     * 读取出库确认后的真实拆批结果，后续调入完全按这个结果落库
     */
    private List<AllocateSplitDetail> buildSplitDetailList(Long warehouseOutId) {
        List<Map<String, Object>> splitRows = materialWarehouseInOutRelMapper.selectSplitInDetailsByWarehouseOutId(warehouseOutId);
        if (CollectionUtils.isEmpty(splitRows)) {
            throw new BusinessRuntimeException("未获取到调拨出库拆批结果，无法生成调入明细");
        }
        List<AllocateSplitDetail> result = new ArrayList<>();
        for (Map<String, Object> row : splitRows) {
            AllocateSplitDetail detail = new AllocateSplitDetail();
            detail.setWarehouseOutDetailId(toLong(row.get("warehouseOutDetailId")));
            detail.setMaterialId(toLong(row.get("materialId")));
            detail.setMaterialCode(toStringValue(row.get("materialCode")));
            detail.setMaterialName(toStringValue(row.get("materialName")));
            detail.setSpecification(toStringValue(row.get("specification")));
            detail.setSpecificationDesc(toStringValue(row.get("specificationDesc")));
            detail.setBrand(toStringValue(row.get("brand")));
            detail.setUnitCode(toStringValue(row.get("unitCode")));
            detail.setUnitName(toStringValue(row.get("unitName")));
            detail.setQuantity(toBigDecimal(row.get("quantity")));
            detail.setTaxIncludedUnitPrice(toBigDecimal(row.get("taxIncludedUnitPrice")));
            detail.setTaxIncludedAmount(toBigDecimal(row.get("taxIncludedAmount")));
            result.add(detail);
        }
        return result;
    }

    /**
     * 一个调拨明细仍只保留一个调入明细ID时，取对应调出明细拆批后的第一条调入明细作为追溯入口
     */
    private Long findFirstInDetailIdByOutDetailId(Long outDetailId,
                                                  List<AllocateSplitDetail> splitDetailList,
                                                  List<EMaterialWarehouseInDetailDTO> inDetails) {
        if (outDetailId == null || CollectionUtils.isEmpty(splitDetailList) || CollectionUtils.isEmpty(inDetails)) {
            return null;
        }
        for (int i = 0; i < splitDetailList.size() && i < inDetails.size(); i++) {
            if (outDetailId.equals(splitDetailList.get(i).getWarehouseOutDetailId())) {
                return inDetails.get(i).getId();
            }
        }
        return null;
    }

    /**
     * 临时构造认证上下文，供出入库服务复用当前用户信息
     */
    private void setAuthentication(EMaterialAllocateDTO allocateDTO) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(allocateDTO.getApplyUser());
        userInfo.setUserName(allocateDTO.getApplyUserName());
        userInfo.setDeptId(allocateDTO.getFromCompanyId());
        userInfo.setDeptName(allocateDTO.getFromCompanyName());
        UserAuthorizeInfo authorizeInfo = new UserAuthorizeInfo(userInfo);
        Authentication authentication = new UsernamePasswordAuthenticationToken(authorizeInfo, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 安全获取当前登录人ID
     */
    private Long safeGetLoginUserId() {
        try {
            return SecurityUtils.getLoginUserId();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 安全获取当前登录人姓名
     */
    private String safeGetLoginUserName() {
        try {
            return SecurityUtils.getLoginUserName();
        } catch (Exception e) {
            return null;
        }
    }

    private Long toLong(Object value) {
        return value instanceof Number ? ((Number) value).longValue() : null;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return null;
    }

    private String toStringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    /**
     * 调拨调入拆批明细临时对象
     */
    @Getter
    @Setter
    private static class AllocateSplitDetail {
        private Long warehouseOutDetailId;
        private Long materialId;
        private String materialCode;
        private String materialName;
        private String specification;
        private String specificationDesc;
        private String brand;
        private String unitCode;
        private String unitName;
        private BigDecimal quantity;
        private BigDecimal taxIncludedUnitPrice;
        private BigDecimal taxIncludedAmount;
    }
}
