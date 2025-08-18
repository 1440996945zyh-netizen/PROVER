package com.yy.ppm.business.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.concurrent.DistributedReentrantLock;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.business.mapper.TBusCargoInfoMapper;
import com.yy.ppm.business.mapper.TBusCargoTransferMapper;
import com.yy.ppm.business.mapper.TBusRateMapper;
import com.yy.ppm.business.service.TBusCargoTransferService;
import com.yy.ppm.common.enums.*;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.common.service.BusinessCommonService;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import com.yy.ppm.statement.bean.dto.TMiscBillingDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import com.yy.ppm.statement.bean.po.TBusHandoverlistTransferPO;
import com.yy.ppm.statement.bean.po.TMiscBillingPO;
import com.yy.ppm.statement.mapper.MiscBillingMapper;
import com.yy.ppm.statement.mapper.TBizCostStatementMapper;
import com.yy.ppm.statement.mapper.TBusHandoverlistTransferMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName 票货信息表(TBusCargoInfo)ServiceImpl
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月03日 18:47:00
 */
@Service
public class TBusCargoTransferServiceImpl implements TBusCargoTransferService {

    @Resource
    private TBusCargoInfoMapper tBusCargoInfoMapper;
    @Resource
    private TBusCargoTransferMapper tBusCargoTransferMapper;
    @Resource
    private BusinessCommonService businessCommonService;
    @Resource
    private Snowflake snowflake;
    @Resource
    private CommonService commonService;
    @Resource
    private CommonMapper commonMapper;
    @Resource
    private SecurityUtils securityUtils;
    @Resource
    private SysFileService sysFileService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private MiscBillingMapper miscBillingMapper;
    @Resource
    private TBusRateMapper tBusRateMapper;
    @Resource
    private TBusHandoverlistTransferMapper tBusHandoverlistTransferMapper;
    @Resource
    private TBizCostStatementMapper tBizCostStatementMapper;

    private static final String IS_CLEAR = "1"; // 完货状态-已完货
    private static final String CLEAN_MASS_SIGN_NO = "0"; // 是否清场-否
    private static final String IS_BILLING = "1"; // 是否计费-是
    private static final String SOURCE = "30"; // 票货来源：货权转移
    private static final String PACKING_PIECES = "02"; // 件货
    private static final String PROCESS_CODE_CARGO_TRANS = "1028"; // 作业过程-货权转移
    private static final String PROCESS_NAME_CARGO_TRANS = "货权转移"; // 作业过程-货权转移
    private static final String TRUST_TYPE_XC = "卸船"; // 通知单类型-卸船
    private static final String TRUST_TYPE_JG = "集港"; // 通知单类型-集港
    private static final String TRUST_TYPE_CXJG = "拆箱集港"; // 通知单类型-拆箱集港
    private static final String VOYAGE_LOAD_UNLOAD = "卸"; // 航次装卸船-卸船
    private static final String RATE_ITEM_GKBG = "02"; // 港口包干费
    private static final String RATE_ITEM_STATUS = "10"; // 费率审核状态-已审核
    private static final String HANDOVER_TYPE_1 = "1"; // 装卸船清单
    private static final Integer MISC_STATUS = 10; // 杂项费审核状态-未发布

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TBusCargoTransferDTO> getList(TBusCargoTransferSearchDTO searchDTO) {
    	Pages<TBusCargoTransferDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tBusCargoTransferMapper.getPage(searchDTO);
		});
        return pages;
    }

    /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     @Override
     public TBusCargoTransferDTO getDetail(Long id) {
         return tBusCargoTransferMapper.getById(id);
     }

    /**
     * 保存货转
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TBusCargoTransferDTO dto) {
        if(CollectionUtils.isEmpty(dto.getFileIds())){
            throw new BusinessRuntimeException("请先上传文件附件！");
        }
        // 修改的场合，已经审核的不能修改
        if (dto.getId() != null) {
            // 旧的货转记录
            TBusCargoTransferDTO oldData = tBusCargoTransferMapper.getById(dto.getId());
            if (oldData == null) {
                throw new BusinessRuntimeException("货转记录不存在~");
            }
            if (CargoTransferEnum.BUSINESS_APPROVE.getCode().equals(oldData.getStatus())) {
                throw new BusinessRuntimeException("审批通过的不能修改~");
            }
        }
        // 查询原票货
        TBusCargoInfoDTO sourceCargoInfo = tBusCargoInfoMapper.getById(dto.getCargoInfoIdSource());
        if (sourceCargoInfo == null) {
            throw new BusinessRuntimeException("原票货不存在~");
        }
        if (IS_CLEAR.equals(sourceCargoInfo.getIsClear())) {
            throw new BusinessRuntimeException("已完货票货不能进行货转操作~");
        }
        BigDecimal surplusRightsQuantity =
                sourceCargoInfo.getSurplusRightsQuantity() == null ? BigDecimal.ZERO : sourceCargoInfo.getSurplusRightsQuantity();
        if (dto.getTon().compareTo(surplusRightsQuantity) > 0) {
            throw new BusinessRuntimeException("原票货剩余货权量不足，请重新输入货转重量~");
        }
        int count = 0;
        if (dto.getId() == null) { // 新增
            dto.setId(snowflake.nextId());
            dto.setStatus(CargoTransferEnum.TODO.getCode());
            count = tBusCargoTransferMapper.insert(dto);
        } else { // 修改
            count = tBusCargoTransferMapper.update(dto);
        }
        // 附件信息保存
        sysFileService.saveFileBusRelation(dto.getFileIds(), dto.getId());
        return count == 1;

    }

    /**
     * 库场场存变更
     * @param dto
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean yardApprove(TBusCargoStorageTransferDTO dto) {
        if (CollectionUtils.isEmpty(dto.getStorageList())) {
            throw new BusinessRuntimeException("未查到原票货港存动态~");
        }
        List<TBusCargoStorageDTO> filterList = dto.getStorageList()
                .stream().filter(v1 -> v1.getTransferTon() != null && v1.getTransferTon().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filterList)) {
            throw new BusinessRuntimeException("未填写转移吨数~");
        }
        TBusCargoTransferDTO cargoTransferDTO = tBusCargoTransferMapper.getById(dto.getId());
        List<TPrdPortStorageDetailPO> portStorageDetails = new ArrayList<>();
        int quantityTotal = 0;
        BigDecimal tonTotal = BigDecimal.ZERO;
        for (TBusCargoStorageDTO item : filterList) {
            if (!CLEAN_MASS_SIGN_NO.equals(item.getCleanMassSign())) {
                throw new BusinessRuntimeException("港存已清场，无法转移~");
            }
            if (item.getTransferQuantity() != null) {
                quantityTotal = quantityTotal + item.getTransferQuantity();
                int currQuantity = item.getQuantity() == null ? 0 : item.getQuantity();
                if (item.getTransferQuantity() > currQuantity) {
                    throw new BusinessRuntimeException("货转件数不能大于当前件数~");
                }
            }
            if (item.getTransferTon() != null) {
                tonTotal = tonTotal.add(item.getTransferTon());
                BigDecimal currTon = item.getTon() == null ? BigDecimal.ZERO : item.getTon();
                if (item.getTransferTon().compareTo(currTon) > 0) {
                    throw new BusinessRuntimeException("货转吨数不能大于当前吨数~");
                }
            }
            TPrdPortStorageDetailPO detailPO = new TPrdPortStorageDetailPO();
            detailPO.setId(snowflake.nextId());
            detailPO.setPortStorageId(item.getId());
            detailPO.setCargoInfoId(cargoTransferDTO.getCargoInfoIdSource());
            detailPO.setWorkDate(cargoTransferDTO.getTransferDate());
            detailPO.setClassCode(ClassCodeEnum.DAY.getCode());
            detailPO.setClassName(ClassCodeEnum.DAY.getName());
            detailPO.setProcessDetailCode(PROCESS_CODE_CARGO_TRANS);
            detailPO.setProcessDetailName(PROCESS_NAME_CARGO_TRANS);
            detailPO.setStorehouseId(item.getStorehouseId());
            detailPO.setStorehouseName(item.getStorehouseName());
            detailPO.setRegionId(item.getRegionId());
            detailPO.setRegionName(item.getRegionName());
            detailPO.setMassId(item.getMassId());
            detailPO.setMassName(item.getMassName());
            if (item.getTransferQuantity() != null) {
                detailPO.setQuantity(item.getTransferQuantity() * (-1));
            }
            detailPO.setTon(item.getTransferTon().multiply(new BigDecimal("-1")));
            detailPO.setInoutType(InoutTypeEnum._1.getCode());
            detailPO.setInoutDate(cargoTransferDTO.getTransferDate());
            detailPO.setInoutStorageCode(InoutStorageEnum._60.getCode());
            detailPO.setInoutStorageName(InoutStorageEnum._60.getLabel());
            detailPO.setCompanyId(cargoTransferDTO.getCompanyId());
            detailPO.setCompanyName(cargoTransferDTO.getCompanyName());
            detailPO.setCleanMassSign(CLEAN_MASS_SIGN_NO);
            detailPO.setCargoTransferId(dto.getId());
            portStorageDetails.add(detailPO);
        }
        // 判断港存转移量是否和货转量相等
        Long transferQuantity = cargoTransferDTO.getQuantity() == null ? 0L : cargoTransferDTO.getQuantity();
        if (quantityTotal != transferQuantity.intValue()) {
            throw new BusinessRuntimeException("港存转移件数与货转件数不符，无法转移~");
        }
        if (tonTotal.compareTo(cargoTransferDTO.getTon()) != 0) {
            throw new BusinessRuntimeException("港存转移吨数与货转吨数不符，无法转移~");
        }
        businessCommonService.insertPortStorageDetail(portStorageDetails);
        portStorageDetails.stream().forEach(item -> {
            item.setId(snowflake.nextId());
            item.setPortStorageId(null);
            item.setInoutType(InoutTypeEnum._2.getCode());
            if (item.getQuantity() != null) {
                item.setQuantity(item.getQuantity() * (-1));
            }
            item.setTon(item.getTon().multiply(new BigDecimal("-1")));
            item.setCargoInfoId(cargoTransferDTO.getCargoInfoIdTarget());
        });
        businessCommonService.insertPortStorageDetail(portStorageDetails);
        // 修改货转记录状态
        UserInfo currentUser = securityUtils.getUserInfo();
        TBusCargoTransferDTO transferDTO = new TBusCargoTransferDTO();
        transferDTO.setId(dto.getId());
        transferDTO.setYardApprovalBy(currentUser.getId());
        transferDTO.setYardApprovalByName(currentUser.getUserName());
        transferDTO.setYardApprovalTime(new Date());
        transferDTO.setStatus(CargoTransferEnum.YARD_APPROVE.getCode());
        tBusCargoTransferMapper.yardApprove(transferDTO);
        return true;
    }
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean yardCancelApprove (TBusCargoTransferDTO cargoTransferDTO) {
        TBusCargoTransferDTO dto = tBusCargoTransferMapper.getById(cargoTransferDTO.getId());
        if (!CargoTransferEnum.YARD_APPROVE.getCode().equals(dto.getStatus())) {
            throw new BusinessRuntimeException("未审批，无法撤销~");
        }
        // 查询原票货货转垛位港存是否清场
        List<TBusCargoStorageDTO> allSourceStorageMassList =
                tBusCargoTransferMapper.getAllStorageList(dto.getCargoInfoIdSource(), dto.getId(), null);
        Set<Long> massIdSet = allSourceStorageMassList.stream().map(TBusCargoStorageDTO::getMassId).collect(Collectors.toSet());
        List<TBusCargoStorageDTO> allSourceStorageList =
                tBusCargoTransferMapper.getAllStorageList(dto.getCargoInfoIdSource(), null, new ArrayList<>(massIdSet));
        allSourceStorageList.stream().forEach(item -> {
            if (!CLEAN_MASS_SIGN_NO.equals(item.getCleanMassSign())) {
                throw new BusinessRuntimeException("原票货港存已清场，无法撤销~");
            }
        });
        // 查询目标票货垛位是否包含其他港存动态
        List<TBusCargoStorageDTO> allTargetStorageMassList =
                tBusCargoTransferMapper.getAllStorageList(dto.getCargoInfoIdTarget(), null, null);
        allTargetStorageMassList.stream().forEach(item -> {
            if (!CLEAN_MASS_SIGN_NO.equals(item.getCleanMassSign())) {
                throw new BusinessRuntimeException("目标票货港存已清场，无法撤销~");
            }
            if (item.getCargoTransferId() == null || !dto.getId().equals(item.getCargoTransferId())) {
                throw new BusinessRuntimeException("目标票货港存已发生变化，无法撤销~");
            }
        });
        // 删除原票货因货转生成的出库场存动态
        List<Long> sourceStorageIds = allSourceStorageMassList.stream().map(TBusCargoStorageDTO::getId)
                .collect(Collectors.toList());
        businessCommonService.deletePortStorageDetail(sourceStorageIds);
        // 删除目标票货的场存
        List<Long> targetStorageIds = allTargetStorageMassList.stream().map(TBusCargoStorageDTO::getId)
                .collect(Collectors.toList());
        businessCommonService.deletePortStorageDetail(targetStorageIds);
        // 更新货转状态
        TBusCargoTransferDTO transferDTO = new TBusCargoTransferDTO();
        transferDTO.setId(dto.getId());
        transferDTO.setYardApprovalBy(null);
        transferDTO.setYardApprovalByName(null);
        transferDTO.setYardApprovalTime(null);
        transferDTO.setStatus(CargoTransferEnum.BUSINESS_APPROVE.getCode());
        tBusCargoTransferMapper.yardApprove(transferDTO);
        return true;
    }

    /**
     * 删除货转
     *
     * @param  id
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {

        TBusCargoTransferDTO oldData = tBusCargoTransferMapper.getById(id);

        if (oldData == null) {
            throw new BusinessRuntimeException("货转记录不存在~");
        }

        // 1、判断票货在指令是否存在
        int tempCount = commonMapper.getCount("T_BUS_TRUST_CARGO", "CARGO_INFO_ID", StringUtil.getString(oldData.getCargoInfoIdTarget()));

        if (tempCount > 0) {
            throw new BusinessRuntimeException("目标票货已经生成指令不能删除~");
        }

        return deleteTransfer(oldData) == 1;

    }

    /**
     * 通用货转删除
     * @param oldData
     * @return
     */
    private int deleteTransfer(TBusCargoTransferDTO oldData) {

        /* 暂时注释 2023.11.23
        // 1 既存的目标票货减少
        businessCommonService.updateSurplusBusCargoInfo(oldData.getCargoInfoIdTarget(), oldData.getQuantity(), oldData.getTon(), "-");

        // 2 源票货增加
        businessCommonService.updateSurplusBusCargoInfo(oldData.getCargoInfoIdSource(), oldData.getQuantity(), oldData.getTon(), "+");

        // 3 查询目标票货
        TBusCargoInfoDTO targetCargoInfo = tBusCargoInfoMapper.getById(oldData.getCargoInfoIdTarget());

        // 如果目标票货的吨 == 0 删除目标票货
        if (targetCargoInfo != null && targetCargoInfo.getTon().compareTo(BigDecimal.ZERO) == 0) {
            tBusCargoInfoMapper.deleteById(oldData.getCargoInfoIdTarget());
        }*/

        if (CargoTransferEnum.BUSINESS_APPROVE.getCode().equals(oldData.getStatus())) {
            throw new BusinessRuntimeException("货转已审批，无法删除");
        }
        // 4 删除货转
        return tBusCargoTransferMapper.deleteById(oldData.getId());

    }

    /**
     * 保存审核
     *
     * @param transferDTO
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doApprove(TBusCargoTransferDTO transferDTO) {
        String key = DistributedLockKeyPrefixEnum.CARGO_INFO_TRANSFER_KEY.getCode()
                + "-" + String.format("%s", transferDTO.getId());
        redisTemplate.opsForValue().set(key, "1");
        DistributedReentrantLock.newBuilder().store(redisTemplate).key(
                DistributedLockKeyPrefixEnum.CARGO_INFO_TRANSFER_KEY.getCode() + String.format("%s", transferDTO.getId()))
                .build().run(() -> {
                    boolean flag = true;
                    String trustType = "";
                    // 货转记录
                    TBusCargoTransferDTO dto = tBusCargoTransferMapper.getById(transferDTO.getId());
                    if (CargoTransferEnum.BUSINESS_APPROVE.getCode().equals(dto.getStatus())) {
                        throw new BusinessRuntimeException("审批通过的不能再审批~");
                    }
                    // 查询原票货
                    TBusCargoInfoDTO sourceCargoVoyage = tBusCargoInfoMapper.getCargoVoyageById(dto.getCargoInfoIdSource());
                    if (sourceCargoVoyage == null) {
                        throw new BusinessRuntimeException("原票货不存在~");
                    }
                    trustType = sourceCargoVoyage.getTrustType();
                    BigDecimal surplusRightsQuantity
                            = sourceCargoVoyage.getSurplusRightsQuantity() == null ? BigDecimal.ZERO : sourceCargoVoyage.getSurplusRightsQuantity();
                    if (dto.getTon().compareTo(surplusRightsQuantity) > 0) {
                        throw new BusinessRuntimeException("原票货剩余货权量不足~");
                    }
                    /*if (StringUtils.isEmpty(sourceCargoVoyage.getTrustType())) {
                        throw new BusinessRuntimeException("原票货未匹配到指令或指令类型为空~");
                    }*/
                    // 查询已转件数
                    if (PACKING_PIECES.equals(sourceCargoVoyage.getPackingCode()) && sourceCargoVoyage.getQuantity() != null) {
                        Long transferQuantity = tBusCargoInfoMapper.getQuantityByParentId(sourceCargoVoyage.getId());
                        Long currTransQuantity = dto.getQuantity() == null ? 0L : dto.getQuantity();
                        if (sourceCargoVoyage.getQuantity() - transferQuantity - currTransQuantity < 0) {
                            throw new BusinessRuntimeException("原票货剩余件数不足~");
                        }
                    }
                    // 判断原票货是否由货转生成，分别计算剩余免堆存期
                    Integer residueStorage = 0;
                    if (sourceCargoVoyage.getParentId() == null) { // 原票货非货转票货
                        if (StringUtils.isEmpty(trustType)) {
                            if (StringUtils.isEmpty(transferDTO.getCargoSource()) || transferDTO.getSourceStorageDate() == null) {
                                // 若客户未录入票货来源和免堆存期起算日期
                                if (!StringUtils.isEmpty(sourceCargoVoyage.getLoadUnload())
                                        && VOYAGE_LOAD_UNLOAD.equals(sourceCargoVoyage.getLoadUnload())) {
                                    // 若航次为进口卸船
                                    residueStorage = getXCFreeStorage(dto, sourceCargoVoyage);
                                } else {
                                    // 若票货没有航次信息，根据票货号查找是否存在过磅记录
                                    List<TBusCargoWeightDTO> weightList = tBusCargoTransferMapper
                                            .getWeightByCargoInfo(sourceCargoVoyage.getId());
                                    if (CollectionUtils.isEmpty(weightList)) {
                                        // 如果票货没有航次且查不到过磅记录，返回让客户填写票货来源和免堆存期起算日期
                                        flag = false;
                                        redisTemplate.opsForValue().set(key, "0");
                                    } else {
                                        // 查询合同免堆存期,以第一次过磅时间为准
                                        Date startWeighDate = DateUtils.formatDate(weightList.get(0).getWeighOutDt(), 0, 0, 0);
                                        Integer freeStorage = getContractFreeStorage(sourceCargoVoyage, startWeighDate);
                                        // 计算剩余免堆存期
                                        residueStorage = getResidueTonStorage(dto, sourceCargoVoyage, weightList, freeStorage);
                                    }
                                }
                            } else {
                                // 若客户录入了票货来源和免堆存期起算日期
                                trustType = transferDTO.getCargoSource();
                                Integer freeStorage = getContractFreeStorage(sourceCargoVoyage, transferDTO.getSourceStorageDate());
                                Integer days = DateUtils.timeDiffDays(new DateTime(transferDTO.getSourceStorageDate()),
                                        new DateTime(DateUtils.formatDate(dto.getTransferDate(), 0,0,0)));
                                residueStorage = freeStorage - days;
                            }
                        } else {
                            // 若原票货匹配到指令
                            residueStorage = countResidueStorage(dto, sourceCargoVoyage);
                        }
                    } else { // 原票货是货转票货
                        residueStorage = countTransferResidueStorage(dto, sourceCargoVoyage);
                    }
                    if (flag) {
                        // 更新原票货剩余货权量
                        TBusCargoInfoDTO oldCargoInfo = new TBusCargoInfoDTO();
                        oldCargoInfo.setId(sourceCargoVoyage.getId());
                        oldCargoInfo.setSurplusRightsQuantity(sourceCargoVoyage.getSurplusRightsQuantity().subtract(dto.getTon()));
                        tBusCargoInfoMapper.updateSurplusRightsQuantity(oldCargoInfo);
                        // 生成新的目标票货
                        sourceCargoVoyage.setParentId(sourceCargoVoyage.getId());
                        if (sourceCargoVoyage.getRootId() == null) {
                            sourceCargoVoyage.setRootId(sourceCargoVoyage.getId());
                        } else {
                            sourceCargoVoyage.setRootId(sourceCargoVoyage.getRootId());
                        }
                        sourceCargoVoyage.setId(snowflake.nextId());
                        // 指令编号
                        sourceCargoVoyage.setCargoInfoNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.SUB_CARGO_INFO, sourceCargoVoyage.getCargoInfoNo()));
                        //sourceCargoInfo.setCargoInfoNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.MAIN_CARGO_INFO, null));
                        sourceCargoVoyage.setTon(dto.getTon());
                        sourceCargoVoyage.setQuantity(dto.getQuantity());
                        sourceCargoVoyage.setRightsQuantity(dto.getTon());
                        sourceCargoVoyage.setSurplusRightsQuantity(dto.getTon());
                        sourceCargoVoyage.setCargoAgentId(dto.getCargoAgentId());
                        sourceCargoVoyage.setCargoAgentName(dto.getCargoAgentName());
                        sourceCargoVoyage.setCargoOwnerId(dto.getCargoOwnerId());
                        sourceCargoVoyage.setCargoOwnerName(dto.getCargoOwnerName());
                        sourceCargoVoyage.setStorageDate(dto.getStorageDate());
                        sourceCargoVoyage.setSource(SOURCE);
                        sourceCargoVoyage.setStatementStatusCode(HandoverlistStatusEnum._10.getCode());
                        sourceCargoVoyage.setStatementStatusName(HandoverlistStatusEnum._10.getName());
                        sourceCargoVoyage.setClearBy(null);
                        sourceCargoVoyage.setClearByName(null);
                        sourceCargoVoyage.setClearDate(null);
                        sourceCargoVoyage.setContractCode(null);
                        sourceCargoVoyage.setContractItemId(null);
                        sourceCargoVoyage.setResidueStorage(residueStorage);
                        sourceCargoVoyage.setTransferDate(dto.getTransferDate());
                        tBusCargoInfoMapper.insert(sourceCargoVoyage);

                        // 设置目标票货id
                        dto.setCargoInfoIdTarget(sourceCargoVoyage.getId());
                        // 更新货权转移状态
                        TBusCargoTransferDTO approvalDto = new TBusCargoTransferDTO();
                        approvalDto.setId(dto.getId());
                        approvalDto.setStatus(CargoTransferEnum.BUSINESS_APPROVE.getCode());
                        approvalDto.setCargoInfoIdTarget(dto.getCargoInfoIdTarget());
                        approvalDto.setCargoSource(transferDTO.getCargoSource());
                        approvalDto.setSourceStorageDate(transferDTO.getSourceStorageDate());
                        int count = tBusCargoTransferMapper.approve(approvalDto);
                        // 判断是否计费，若计费，根据费率生成包干费（货权转移）杂项费
                        if (IS_BILLING.equals(dto.getIsBilling())) {
                            if (dto.getPaymentCustomerId() == null) {
                                throw new BusinessRuntimeException("货权转移计费付款人为空，无法计费");
                            }
                            addMiscBilling(dto,  sourceCargoVoyage);
                        }
                        // 判断是否进口卸船，生成进口卸船交接清单(货权转移)
                        //if (TRUST_TYPE_XC.equals(trustType)) {
                            addHandoverListTransfer(sourceCargoVoyage);
                        //}
                    }
                });
        return "1".equals(redisTemplate.opsForValue().get(key));
        /*  暂不考虑合并票货的情况  2023.11.23
        // 2. 生成目标票货
        // 判断目标票货是否存在
        Map<String, Object> map = BeanUtil.beanToMap(dto);
        map.put("id", null);
        map.put("companyId", sourceCargoInfo.getCompanyId());
        map.put("cargoCode", sourceCargoInfo.getCargoCode());
        map.put("tradeType", sourceCargoInfo.getTradeType());
        map.put("packingCode", sourceCargoInfo.getPackingCode());
        Long targetBusCargoInoId = businessCommonService.getBusCargoInfoId(map);
        // 既存的目标票货增加
        businessCommonService.updateSurplusBusCargoInfo(targetBusCargoInoId, dto.getQuantity(), dto.getTon(), "+");
        // 源票货减少
        businessCommonService.updateSurplusBusCargoInfo(sourceCargoInfo.getId(), dto.getQuantity(), dto.getTon(), "-");
        */
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean cancelApprove (TBusCargoTransferDTO cargoTransferDTO) {
        TBusCargoTransferDTO dto = tBusCargoTransferMapper.getById(cargoTransferDTO.getId());
        if("1".equals(dto.getStatus())){
            throw new BusinessRuntimeException("货转状态还是未审核，无法撤销~");
        }
        //判断是否已经下通知单
        List<TBusTrustDTO> tmpTrustList  = tBusCargoTransferMapper.getTrustByCargoInfoId(cargoTransferDTO);

        if(!CollectionUtils.isEmpty(tmpTrustList)){
            throw new BusinessRuntimeException("目标票货已产生通知单，无法撤销~");
        }


        if (CargoTransferEnum.YARD_APPROVE.getCode().equals(dto.getStatus())) {
            throw new BusinessRuntimeException("库场已审批，无法撤销~");
        }
        if (CargoTransferEnum.TODO.getCode().equals(dto.getStatus())) {
            throw new BusinessRuntimeException("未审批，无法撤销~");
        }
        // 删除货转杂项计费
        if (IS_BILLING.equals(dto.getIsBilling())) {
            List<TMiscBillingDTO> transFeeList =
                    miscBillingMapper.getListByStatementIdAndRate(dto.getId(), PROCESS_CODE_CARGO_TRANS);
            if (!CollectionUtils.isEmpty(transFeeList)) {
                transFeeList.stream().forEach(item -> {
                    if (!MISC_STATUS.equals(item.getStatus())) {
                        throw new BusinessRuntimeException("货权转移杂项费用已审核，请先联系计费中心撤销审核");
                    }
                    miscBillingMapper.deleteMisc(item.getId());
                });
            } else {
                //此处不做校验，杂项计费处可以手动删除该费用
                //throw new BusinessRuntimeException("未找到货转杂项计费，无法撤销~");
            }
        }
        // TODO 查询是否已结算,暂时只查询了原票货堆存费结算
        List<TCostStatementDetailDTO> statementDetailList =
                tBizCostStatementMapper.getStatementDetailByCargoInfoId(dto.getCargoInfoIdSource());
        if (!CollectionUtils.isEmpty(statementDetailList)) {
            throw new BusinessRuntimeException("原票货已生成堆存费结算单，无法撤销~");
        }
        // 如果是卸船，删除交接清单(货权转移)
        String trustType = dto.getCargoSource();
        TBusCargoInfoDTO sourceCargoVoyage = tBusCargoInfoMapper.getCargoVoyageById(dto.getCargoInfoIdSource());
        if (IS_CLEAR.equals(sourceCargoVoyage.getIsClear())) {
            throw new BusinessRuntimeException("原票货已完货，无法撤销~");
        }
        if (StringUtils.isEmpty(trustType)) {
            trustType = sourceCargoVoyage.getTrustType();
        }
        if (TRUST_TYPE_XC.equals(trustType)) {
            tBusHandoverlistTransferMapper.deleteByCargoInfoId(dto.getCargoInfoIdTarget());
        }
        // 删除目标票货
        tBusCargoInfoMapper.deleteById(dto.getCargoInfoIdTarget());
        // 还原原票货货量
        TBusCargoInfoDTO oldCargoInfo = new TBusCargoInfoDTO();
        oldCargoInfo.setId(sourceCargoVoyage.getId());
        oldCargoInfo.setSurplusRightsQuantity(sourceCargoVoyage.getSurplusRightsQuantity().add(dto.getTon()));
        tBusCargoInfoMapper.updateSurplusRightsQuantity(oldCargoInfo);
        // 更改货转记录状态
        TBusCargoTransferDTO approvalDto = new TBusCargoTransferDTO();
        approvalDto.setId(dto.getId());
        approvalDto.setStatus(CargoTransferEnum.TODO.getCode());
        approvalDto.setCargoInfoIdTarget(dto.getCargoInfoIdTarget());
        approvalDto.setCargoSource(null);
        approvalDto.setSourceStorageDate(null);
        int count = tBusCargoTransferMapper.approve(approvalDto);
        return true;
    }

    @Override
    public List<TBusCargoStorageDTO> getStorageList(Long cargoInfoId) {
        return tBusCargoTransferMapper.getStorageList(cargoInfoId);
    }

    /**
     * 判断是否进口卸船，生成货权转移杂项计费(货权转移)
     */
    private void addMiscBilling(TBusCargoTransferDTO dto, TBusCargoInfoDTO cargoSourceVoyage) {
        // 查询 港口作业包干费-货权转移 费率
        TBusRateSearchDTO searchDTO = new TBusRateSearchDTO();
        searchDTO.setRateItemCode(RATE_ITEM_GKBG);
        searchDTO.setProcessCode(PROCESS_CODE_CARGO_TRANS);
        searchDTO.setStatus(RATE_ITEM_STATUS);
        searchDTO.setCurrTime(DateUtils.formatDate(dto.getTransferDate(), "yyyy-MM-dd"));
        List<TBusRateDTO> rateList = tBusRateMapper.getBusRateList(searchDTO);
        if (!CollectionUtils.isEmpty(rateList)) {
            if (rateList.size() != 1) {
                throw new BusinessRuntimeException("港口作业包干费（货权转移）费率不唯一，无法计费");
            }
            TBusRateDTO tBusRateDTO = rateList.get(0);
            TMiscBillingPO miscBillingPO = new TMiscBillingPO();
            miscBillingPO.setId(snowflake.nextId());
            miscBillingPO.setRateItemCode(tBusRateDTO.getRateItemCode());
            miscBillingPO.setRateName(tBusRateDTO.getRateItemName());
            miscBillingPO.setRate(tBusRateDTO.getRate());
            miscBillingPO.setTaxRate(tBusRateDTO.getTaxRate());
            miscBillingPO.setBillDate(dto.getTransferDate());
            miscBillingPO.setVoyageId(cargoSourceVoyage.getShipvoyageItemId());
            miscBillingPO.setShipVoyage(cargoSourceVoyage.getShipNameVoyage());
            miscBillingPO.setBillQuantity(dto.getTon());
            miscBillingPO.setAmountMoney(dto.getTon().multiply(tBusRateDTO.getRate()));
            BigDecimal taxRate = tBusRateDTO.getTaxRate().divide(new BigDecimal("100"));
            miscBillingPO.setTaxAmount(miscBillingPO.getAmountMoney()
                    .divide(taxRate.add(new BigDecimal("1")),9,BigDecimal.ROUND_HALF_UP)
                    .multiply(taxRate).setScale(2, BigDecimal.ROUND_HALF_UP));
            miscBillingPO.setCustomerId(dto.getPaymentCustomerId());
            miscBillingPO.setStatus(MISC_STATUS);
            miscBillingPO.setCompanyId(cargoSourceVoyage.getCompanyId());
            miscBillingPO.setCompanyName(cargoSourceVoyage.getCompanyName());
            miscBillingPO.setRateId(tBusRateDTO.getId());
            miscBillingPO.setUnitCode(tBusRateDTO.getMeasurementUnitCode1());
            miscBillingPO.setUnitName(tBusRateDTO.getMeasurementUnitName1());
            miscBillingPO.setProcessCode(PROCESS_CODE_CARGO_TRANS);
            miscBillingPO.setProcessName(PROCESS_NAME_CARGO_TRANS);
            miscBillingPO.setCargoInfoId(dto.getCargoInfoIdTarget());
            miscBillingPO.setOtherStatementId(dto.getId());
            miscBillingPO.setCargoInfoName(cargoSourceVoyage.getCargoInfoNo() + "-"
                    + cargoSourceVoyage.getCargoName() + "-" + cargoSourceVoyage.getTradeType());
            miscBillingMapper.addMiscBilling(miscBillingPO);
        } else {
            throw new BusinessRuntimeException("未匹配到 港口作业包干费（货权转移）费率");
        }
    }
    /**
     * 判断是否进口卸船，生成进口卸船交接清单(货权转移)
     */
    private void addHandoverListTransfer(TBusCargoInfoDTO cargoSourceVoyage) {
        TBusHandoverlistTransferPO handoverlistPO = new TBusHandoverlistTransferPO();
        handoverlistPO.setId(snowflake.nextId());
        handoverlistPO.setCargoInfoId(cargoSourceVoyage.getId());
        handoverlistPO.setShipvoyageId(cargoSourceVoyage.getShipvoyageId());
        handoverlistPO.setShipvoyageItemId(cargoSourceVoyage.getShipvoyageItemId());
        handoverlistPO.setShipName(cargoSourceVoyage.getShipName());
        handoverlistPO.setTradeType(cargoSourceVoyage.getTradeType());
        handoverlistPO.setLoadUnload(cargoSourceVoyage.getLoadUnload());
        handoverlistPO.setCargoOwnerId(cargoSourceVoyage.getCargoOwnerId());
        handoverlistPO.setCargoOwnerName(cargoSourceVoyage.getCargoOwnerName());
        handoverlistPO.setCargoCode(cargoSourceVoyage.getCargoCode());
        handoverlistPO.setCargoName(cargoSourceVoyage.getCargoName());
        handoverlistPO.setQuantity(cargoSourceVoyage.getQuantity() == null ? null : cargoSourceVoyage.getQuantity().intValue());
        handoverlistPO.setTon(cargoSourceVoyage.getTon());
        handoverlistPO.setStatementStatusCode(HandoverlistStatusEnum._10.getCode());
        handoverlistPO.setStatementStatusName(HandoverlistStatusEnum._10.getName());
        handoverlistPO.setVoyage(cargoSourceVoyage.getVoyage());
        handoverlistPO.setType(HANDOVER_TYPE_1);
        tBusHandoverlistTransferMapper.insertBusHandoverlist(handoverlistPO);
    }
    /**
     * 原票货非货转票货，计算剩余免堆存期
     */
    private Integer countResidueStorage (TBusCargoTransferDTO dto, TBusCargoInfoDTO sourceCargoVoyage) {
        if (TRUST_TYPE_XC.equals(sourceCargoVoyage.getTrustType())) { // 卸船指令
            return getXCFreeStorage(dto, sourceCargoVoyage);
        } else if (TRUST_TYPE_JG.equals(sourceCargoVoyage.getTrustType()) || TRUST_TYPE_CXJG.equals(sourceCargoVoyage.getTrustType())) { // 集港指令
            // 查询票货过磅数据
            List<TBusCargoWeightDTO> weightList = tBusCargoTransferMapper
                    .getWeightByTrustAndCargoInfo(sourceCargoVoyage.getTrustId(), sourceCargoVoyage.getId());
            if (CollectionUtils.isEmpty(weightList)) {
                throw new BusinessRuntimeException("数据异常，未找到过磅记录");
            }
            // 查询合同免堆存期,以第一次过磅时间为准
            Date startWeighDate = DateUtils.formatDate(weightList.get(0).getWeighOutDt(), 0, 0, 0);
            Integer freeStorage = getContractFreeStorage(sourceCargoVoyage, startWeighDate);
            // 计算剩余免堆存期
            return getResidueTonStorage(dto, sourceCargoVoyage, weightList, freeStorage);
        } else {
            throw new BusinessRuntimeException("匹配的通知单类型异常，请业务确认");
        }
    }

    /**
     * 查询进口卸船剩余免堆存期
     * @param dto
     * @param sourceCargoVoyage
     * @return
     */
    private Integer getXCFreeStorage(TBusCargoTransferDTO dto, TBusCargoInfoDTO sourceCargoVoyage) {
        Date berthTime = sourceCargoVoyage.getBerthTime();
        if (berthTime == null) {
            throw new BusinessRuntimeException("未找到船舶靠泊时间，无法计算免堆存期");
        }
        berthTime = DateUtils.formatDate(berthTime, 0,0,0);
        // 根据作业公司、客户、货类、靠泊时间查询合同货物免堆存期
        Integer freeStorage = getContractFreeStorage(sourceCargoVoyage, berthTime);
        Integer days = DateUtils.timeDiffDays(new DateTime(berthTime),
                new DateTime(DateUtils.formatDate(dto.getTransferDate(), 0,0,0)));
        return freeStorage - days;
    }

    /**
     * 查询合同免堆存期
     * @param sourceCargoInfo
     * @param date
     * @return
     */
    private Integer getContractFreeStorage(TBusCargoInfoDTO sourceCargoInfo, Date date) {
        List<TBusCargoFreeStorageDTO> freeStorageDTOList = tBusCargoTransferMapper
                .getCargoFreeStorageDays(sourceCargoInfo.getCompanyId(), sourceCargoInfo.getCargoOwnerId(),
                        sourceCargoInfo.getCargoCode(), DateUtils.formatDate(date, "yyyy-MM-dd"));
        if (!CollectionUtils.isEmpty(freeStorageDTOList)) {
                /*if (freeStorageDTOList.size() > 1) {
                    throw new BusinessRuntimeException("匹配到了多个合同货物费率，无法计算免堆存期");
                }*/
            return freeStorageDTOList.get(0).getFreeStorageDays();
        } else {
            throw new BusinessRuntimeException("未找到合同货物费率，无法计算免堆存期");
        }
    }
    /**
     * 货转票货，计算免堆存期
     */
    private Integer countTransferResidueStorage (TBusCargoTransferDTO dto, TBusCargoInfoDTO sourceCargoVoyage) {
        /*if (TRUST_TYPE_XC.equals(sourceCargoVoyage.getTrustType())
                || TRUST_TYPE_JG.equals(sourceCargoVoyage.getTrustType())) {

        } else {
            throw new BusinessRuntimeException("匹配的通知单类型异常，请业务确认");
        }*/
        if (sourceCargoVoyage.getTransferDate() == null || sourceCargoVoyage.getResidueStorage() == null) {
            throw new BusinessRuntimeException("数据异常，请联系管理员");
        }
        Date lastTransferDate = DateUtils.formatDate(sourceCargoVoyage.getTransferDate(), 0, 0, 0);
        Date currTransferDate = DateUtils.formatDate(dto.getTransferDate(), 0, 0, 0);
        Integer days = DateUtils.timeDiffDays(new DateTime(lastTransferDate), new DateTime(currTransferDate));
        return sourceCargoVoyage.getResidueStorage() - days;
    }

    /**
     * 计算剩余免堆存期
     * @param dto 货转记录
     * @param sourceCargoInfo 源票货
     * @param weightList 过磅记录
     * @param freeStorage 合同免堆存期
     * @return
     */
    private Integer getResidueTonStorage(TBusCargoTransferDTO dto, TBusCargoInfoDTO sourceCargoInfo,
                                  List<TBusCargoWeightDTO> weightList, Integer freeStorage) {
        Date transferDate = DateUtils.formatDate(dto.getTransferDate(), 0, 0, 0);
        List<TBusCargoWeightDTO> residueWeightList;
        if (sourceCargoInfo.getTon().compareTo(sourceCargoInfo.getSurplusRightsQuantity()) == 0) { // 第一次货转
            residueWeightList = weightList;
        } else { // 多次货转
            BigDecimal transferTon = sourceCargoInfo.getTon().subtract(sourceCargoInfo.getSurplusRightsQuantity());
            residueWeightList = getResidueWeightList(transferTon, weightList);
        }
        BigDecimal weightTon = BigDecimal.ZERO; // 累计过磅吨数
        BigDecimal residueTonStorage = BigDecimal.ZERO; // 剩余过磅吨数堆存期（200*2 + 200*1 + 200*0）
        for (TBusCargoWeightDTO weightDTO : residueWeightList) {
            Date outDate = DateUtils.formatDate(weightDTO.getWeighOutDt(), 0, 0, 0);
            Integer currResidueStorage =
                    freeStorage - DateUtils.timeDiffDays(new DateTime(outDate), new DateTime(transferDate));
            if (dto.getTon().compareTo(weightTon.add(weightDTO.getWeightGoods())) <= 0) {
                // 累计过磅吨数大于货转吨数时 300 250+100（50）
                BigDecimal currWeightTon = dto.getTon().subtract(weightTon); // 本次过磅吨数（货转剩余）
                residueTonStorage = residueTonStorage.add(currWeightTon.multiply(new BigDecimal(currResidueStorage)));
                return residueTonStorage.divide(dto.getTon(),0, BigDecimal.ROUND_HALF_UP).intValue();
            } else { // 累计过磅吨数未大于货转吨数
                BigDecimal currWeightTon = weightDTO.getWeightGoods(); // 本次过磅吨数
                residueTonStorage = residueTonStorage.add(currWeightTon.multiply(new BigDecimal(currResidueStorage)));
                weightTon = weightTon.add(weightDTO.getWeightGoods());
            }
        }
        // 正常情况下不应出现这种情况，即货转吨数不应超过剩余过磅吨数,暂时抛出异常
        throw new BusinessRuntimeException("实际过磅吨数不足，无法货转");
        //return residueTonStorage.divide(dto.getTon(),0, BigDecimal.ROUND_HALF_UP).intValue();
    }

    /**
     * 统计剩余过磅记录
     * @param transferTon
     * @param weightList
     * @return
     */
    private List<TBusCargoWeightDTO> getResidueWeightList(BigDecimal transferTon, List<TBusCargoWeightDTO> weightList) {
        BigDecimal initWeight = BigDecimal.ZERO;
        List<TBusCargoWeightDTO> residueWeightList = new ArrayList<>();
        for (TBusCargoWeightDTO item : weightList) {
            BigDecimal weightGoods = item.getWeightGoods();
            if (transferTon.compareTo(initWeight.add(weightGoods)) < 0) {
                BigDecimal currWeightTon = item.getWeightGoods();
                if (transferTon.compareTo(initWeight) >= 0) {
                    currWeightTon = initWeight.add(weightGoods).subtract(transferTon);
                }
                item.setWeightGoods(currWeightTon);
                residueWeightList.add(item);
            }
            initWeight = initWeight.add(weightGoods);
        }
        return residueWeightList;
    }

}

