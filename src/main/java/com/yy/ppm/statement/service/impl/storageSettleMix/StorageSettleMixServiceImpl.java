package com.yy.ppm.statement.service.impl.storageSettleMix;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.enums.CommonEnum;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtil;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import com.yy.ppm.business.bean.po.TBusContractRatePO;
import com.yy.ppm.common.enums.*;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.statement.bean.dto.storageSettleMix.*;
import com.yy.ppm.statement.bean.po.*;
import com.yy.ppm.statement.mapper.storageSettleMix.StorageSettleMixJGMapper;
import com.yy.ppm.statement.mapper.storageSettleMix.StorageSettleMixMapper;
import com.yy.ppm.statement.mapper.storageSettleMix.StorageSettleMixXCMapper;
import com.yy.ppm.statement.service.storageSettleMix.StorageSettleMixService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.yy.common.util.DateUtil.date2LocalDate;
import static com.yy.common.util.DateUtil.localDate2Date;
import static java.math.BigDecimal.ZERO;

@Service
public class StorageSettleMixServiceImpl implements StorageSettleMixService {

    @Autowired
    private StorageSettleMixMapper mapper;

    @Autowired
    private StorageSettleMixXCMapper xcMapper;

    @Autowired
    private StorageSettleMixJGMapper jgMapper;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private CommonService commonService;

    @Override
    public Pages<TBusCargoInfoDTO> listCargoInfo(PageParameter parameter, TBusCargoInfoQueryDTO query) {
        return PageHelperUtils.limit(parameter, () -> {
            return mapper.listCargoInfo(query);
        });
    }

    @Override
    public List<TCostStorageSettleDetailDTO> listDetail(Long cargoInfoId) {
        List<TCostStorageSettleDetailDTO> details = listDetailContainsHistory(cargoInfoId);
        return filterHistoryDetail(cargoInfoId, details);
    }

    public List<TCostStorageSettleDetailDTO> listDetailContainsHistory(Long cargoInfoId) {
        TBusCargoInfoDTO cargoInfo = mapper.getCargoInfo2(cargoInfoId);

        List<VWeightInfo> weightInfos = xcMapper.listWeightInfo(cargoInfoId);
        List<Map<String, Object>> loadShipvoyageItems = xcMapper.listLoadShipvoyageItem(cargoInfoId);

        LocalDate beginDate = date2LocalDate(cargoInfo.getMixTime());
        LocalDate endDate = LocalDate.now();
        List<LocalDate> dates = new ArrayList<>();
        LocalDate tempDate = beginDate.minusDays(1);
        while (!(tempDate = tempDate.plusDays(1)).isAfter(endDate)) {
            dates.add(tempDate);
        }

        Pair<Map<LocalDate, BigDecimal>, Map<LocalDate, BigDecimal>> transferInOutTons = getTransferInOutTons(cargoInfoId);
        Map<LocalDate, BigDecimal> mixOutTons = getMixOutTons(cargoInfoId);

        BigDecimal firstdayInTon = cargoInfo.getTon();
        Map<LocalDate, BigDecimal> inTons = dates.stream()
                .map(v1 -> {
                    BigDecimal everydayInTon = ZERO;
                    if (v1.equals(beginDate)) {
                        everydayInTon = firstdayInTon;
                    }
                    return Pair.of(v1, everydayInTon);
                })
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        Map<LocalDate, BigDecimal> outTons = dates.stream().map(v1 -> {
            BigDecimal everydayOutTon = weightInfos.stream()
                    .filter(v2 -> date2LocalDate(v2.getWeighOutDt()).equals(v1))
                    .map(VWeightInfo::getWeightGoods)
                    .reduce(ZERO, BigDecimal::add);
            everydayOutTon = everydayOutTon.add(
                    loadShipvoyageItems.stream()
                            .filter(v2 -> date2LocalDate((Date) v2.get("workEndTime")).equals(v1))
                            .map(v2 -> (BigDecimal) v2.get("ton"))
                            .reduce(ZERO, BigDecimal::add)
            );
            everydayOutTon = everydayOutTon.add(Optional.ofNullable(transferInOutTons.getRight().get(v1)).orElse(ZERO));
            everydayOutTon = everydayOutTon.add(Optional.ofNullable(mixOutTons.get(v1)).orElse(ZERO));
            return Pair.of(v1, everydayOutTon);
        }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        Map<LocalDate, BigDecimal> tons = new HashMap<>();
        for (int i = 0; i < dates.size(); i++) {
            LocalDate date = dates.get(i);
            LocalDate previousDate = dates.stream().filter(v1 -> v1.plusDays(1).equals(date)).findFirst().orElse(null);
            BigDecimal yesterdayTon;
            if (previousDate == null) {
                yesterdayTon = ZERO;
            } else {
                yesterdayTon = tons.get(previousDate);
            }
            BigDecimal ton = yesterdayTon.add(inTons.get(date)).subtract(outTons.get(date));
            tons.put(date, ton);
        }

        return dates.stream()
                .map(v1 -> {
                    TCostStorageSettleDetailDTO detail = new TCostStorageSettleDetailDTO();
                    detail.setDate(localDate2Date(v1));
                    detail.setTon(tons.get(v1));
                    detail.setBillableTon(null);
                    detail.setInTon(inTons.get(v1));
                    detail.setOutTon(outTons.get(v1));
                    detail.setAmount(null);
                    return detail;
                })
                .collect(Collectors.toList());
    }

    private List<TCostStorageSettleDetailDTO> filterHistoryDetail(Long cargoInfoId, List<TCostStorageSettleDetailDTO> details) {
        List<TCostStorageSettleDetailDTO> _details = mapper.listStorageSettleDetail(cargoInfoId, null, null);
        return details.stream().filter(v1 -> _details.stream().noneMatch(v2 -> v1.getDate().equals(v2.getDate()))).collect(Collectors.toList());
    }

    private Pair<Map<LocalDate, BigDecimal>, Map<LocalDate, BigDecimal>> getTransferInOutTons(Long cargoInfoId) {
        TBusCargoInfoPO cargoInfo = mapper.getCargoInfo(cargoInfoId);
        Map<LocalDate, BigDecimal> transferInTons = new HashMap<>();
        Map<LocalDate, BigDecimal> transferOutTons = new HashMap<>();
        if (cargoInfo.getRootId() != null) {
            transferInTons.put(date2LocalDate(cargoInfo.getTransferDate()), Optional.ofNullable(cargoInfo.getTon()).orElse(ZERO));
        }
        List<TBusCargoInfoPO> cargoInfos = mapper.listTransferOutCargoInfo(cargoInfo.getId());
        cargoInfos.forEach(v1 -> {
            transferOutTons.put(date2LocalDate(v1.getTransferDate()), v1.getTon());
        });
        return Pair.of(transferInTons, transferOutTons);
    }

    private Map<LocalDate, BigDecimal> getMixOutTons(Long cargoInfoId) {
        TBusCargoInfoPO cargoInfo = mapper.getCargoInfo(cargoInfoId);
        List<Map<String, Object>> mixOutWeights = mapper.listMixOutWeight(cargoInfo.getId());
        return mixOutWeights.stream()
                .map(v1 -> Pair.of(date2LocalDate((Date) v1.get("mixDate")), (BigDecimal) v1.get("mixWeight")))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    @Override
    public List<Map<String, Object>> listContract(Long cargoInfoId, Date date) {
        return mapper.listContract(cargoInfoId, date);
    }

    private Pair<Boolean, Integer> getReduceDays(Long cargoInfoId) {
        TBusStackFeeReducePO stackFeeReduce = mapper.getStackFeeReduce(cargoInfoId);
        boolean hasReduce = stackFeeReduce != null;
        if (hasReduce) {
            long reduceDays = 0;
            if (ReduceTypeEnum._1.getCode().equals(stackFeeReduce.getReduceType())) {
                reduceDays = stackFeeReduce.getReduceDays();
            }
            if (ReduceTypeEnum._2.getCode().equals(stackFeeReduce.getReduceType())) {
                reduceDays = Math.max(DateUtil.getDateDifference(LocalDate.now(), date2LocalDate(stackFeeReduce.getReduceEndDate())), 0);
            }
            return Pair.of(true, (int) reduceDays);
        }
        return Pair.of(false, null);
    }

    private Pair<Boolean, Integer> getMixFreeStorageDays(Long cargoInfoId) {
        TBusCargoInfoPO cargoInfo = mapper.getCargoInfo(cargoInfoId);
        return Pair.of("50".equals(cargoInfo.getSource()), cargoInfo.getMixFreeStorageDays());
    }

    @Override
    public List<TCostStorageSettleDetailDTO> listDetailWithContract(Long cargoInfoId, Long contractRateId, String isUseReduce) {
        TBusContractRatePO contractRate = mapper.getContractRate(contractRateId);

        List<TCostStorageSettleDetailDTO> details = listDetailContainsHistory(cargoInfoId);

        Map<LocalDate, BigDecimal> inTons = details.stream()
                .map(v1 -> Pair.of(date2LocalDate(v1.getDate()), v1.getInTon()))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (prev, next) -> next, LinkedHashMap::new));

        Pair<Boolean, Integer> reduceDays = getReduceDays(cargoInfoId);
        Pair<Boolean, Integer> mixFreeStorageDays = getMixFreeStorageDays(cargoInfoId);

        List<TCostStorageSettleDetailDTO> finalDetails = details;
        details.forEach(v1 -> {
            BigDecimal inTon = finalDetails.stream()
                    .filter(v2 -> !date2LocalDate(v1.getDate()).minusDays(CommonEnum.YesNoMode.YES.getCode().equals(isUseReduce) && reduceDays.getLeft() ? reduceDays.getRight() : mixFreeStorageDays.getLeft() ? mixFreeStorageDays.getRight() : Optional.ofNullable(contractRate.getFreeStorageDays()).orElse(0)).isBefore(date2LocalDate(v2.getDate())))
                    .map(TCostStorageSettleDetailPO::getInTon)
                    .reduce(ZERO, BigDecimal::add);
            BigDecimal outTon = finalDetails.stream()
                    .filter(v2 -> !date2LocalDate(v1.getDate()).isBefore(date2LocalDate(v2.getDate())))
                    .map(TCostStorageSettleDetailPO::getOutTon)
                    .reduce(ZERO, BigDecimal::add);
            BigDecimal billableTon = inTon.subtract(outTon);
            if (billableTon.compareTo(ZERO) < 0) {
                List<Map.Entry<LocalDate, BigDecimal>> tempInTons = inTons.entrySet().stream()
                        .filter(v2 -> date2LocalDate(v1.getDate()).minusDays(CommonEnum.YesNoMode.YES.getCode().equals(isUseReduce) && reduceDays.getLeft() ? reduceDays.getRight() : mixFreeStorageDays.getLeft() ? mixFreeStorageDays.getRight() : Optional.ofNullable(contractRate.getFreeStorageDays()).orElse(0)).isBefore(v2.getKey())
                                && !date2LocalDate(v1.getDate()).isBefore(v2.getKey()))
                        .collect(Collectors.toList());
                for (Map.Entry<LocalDate, BigDecimal> v2 : tempInTons) {
                    BigDecimal tempBillableTon;
                    boolean bool = (tempBillableTon = billableTon.add(v2.getValue())).compareTo(ZERO) >= 0;
                    billableTon = ZERO.min(tempBillableTon);
                    v2.setValue(ZERO.max(tempBillableTon));
                    if (bool) break;
                }
            }
            v1.setBillableTon(ZERO.max(billableTon));
            v1.setAmount(v1.getBillableTon().multiply(contractRate.getRate()).setScale(2, RoundingMode.HALF_UP));
            v1.setTaxAmount(
                    v1.getAmount().divide(
                            BigDecimal.ONE.add(
                                    contractRate.getTax().divide(
                                            BigDecimal.valueOf(100),
                                            4, RoundingMode.HALF_UP
                                    )
                            ), 100, RoundingMode.HALF_UP
                    ).multiply(
                            contractRate.getTax().divide(
                                    BigDecimal.valueOf(100),
                                    4, RoundingMode.HALF_UP
                            )
                    ).setScale(2, RoundingMode.HALF_UP)
            );
        });
        details = filterHistoryDetail(cargoInfoId, details);
        return details;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void settle(TCostStorageSettleDTO storageSettle) {
        List<TCostStorageSettleDTO> storageSettles = mapper.listStorageSettle(storageSettle.getCargoInfoId());
        if (!storageSettles.isEmpty()) {
            throw new BusinessRuntimeException("当前仅支持结算一次");
        }

        List<TDisShipvoyageItemDTO> shipvoyageItems = jgMapper.listShipvoyageItem(storageSettle.getCargoInfoId());
        if (shipvoyageItems.size() > 1) {
            List<TCostStorageSettlePO> _storageSettles = jgMapper.listStorageSettle(storageSettle.getCargoInfoId());
            for (TDisShipvoyageItemDTO shipvoyageItem : shipvoyageItems) {
                boolean bool = storageSettle.getShipvoyageItemId().equals(shipvoyageItem.getId());
                if (!bool) {
                    bool = _storageSettles.stream().noneMatch(v1 -> shipvoyageItem.getId().equals(v1.getShipvoyageItemId()));
                    if (bool) {
                        throw new BusinessRuntimeException(
                                String.format("当前票货存在多个装船航次，结算日期存在重叠，请先结算先完工的【%s_%s】",
                                        shipvoyageItem.getShipName(),
                                        shipvoyageItem.getVoyage()));
                    }
                } else {
                    break;
                }
            }
        }

        Date startDate = storageSettle.getDetails().stream().min(Comparator.comparing(TCostStorageSettleDetailPO::getDate)).orElseThrow(null).getDate();
        Date endDate = storageSettle.getDetails().stream().max(Comparator.comparing(TCostStorageSettleDetailPO::getDate)).orElseThrow(null).getDate();
        List<TCostStorageSettleDetailDTO> details = mapper.listStorageSettleDetail(storageSettle.getCargoInfoId(), startDate, endDate);
        LocalDate minDuplicateDate = details.stream().map(v1 -> date2LocalDate(v1.getDate())).min(Comparator.comparing(v1 -> v1)).orElse(null);
        LocalDate maxDuplicateDate = details.stream().map(v1 -> date2LocalDate(v1.getDate())).max(Comparator.comparing(v1 -> v1)).orElse(null);
        if (!details.isEmpty()) {
            throw new BusinessRuntimeException(String.format("重复的结算日期：%s至%s", minDuplicateDate, maxDuplicateDate));
        }

        storageSettle.setId(snowflake.nextId());
        storageSettle.setAmount(storageSettle.getDetails().stream().map(TCostStorageSettleDetailPO::getAmount).reduce(ZERO, BigDecimal::add));
        storageSettle.setTaxAmount(storageSettle.getDetails().stream().map(TCostStorageSettleDetailPO::getTaxAmount).reduce(ZERO, BigDecimal::add));
        storageSettle.setStartDate(startDate);
        storageSettle.setEndDate(endDate);
        storageSettle.setStatus(StorageSettleStatusEnum._10.getCode());
        mapper.insertStorageSettle(storageSettle);

        storageSettle.getDetails().forEach(v1 -> {
            v1.setId(snowflake.nextId());
            v1.setStorageSettleId(storageSettle.getId());
        });
        mapper.insertStorageSettleDetail(storageSettle.getDetails());
    }

    @Override
    public List<TCostStorageSettleDTO> listStorageSettle(Long cargoInfoId) {
        return mapper.listStorageSettle(cargoInfoId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void cancelSettle(Long storageSettleId) {
        TCostStorageSettlePO storageSettle = mapper.getStorageSettle(storageSettleId);
        if (StorageSettleStatusEnum._20.getCode().equals(storageSettle.getStatus())) {
            throw new BusinessRuntimeException("已审核无法撤销结算");
        }

        mapper.deleteStorageSettle(storageSettleId);
        mapper.deleteStorageSettleDetail(storageSettleId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void review(Long storageSettleId) {
        TCostStorageSettlePO tempStorageSettle = mapper.getStorageSettle(storageSettleId);
        if (StorageSettleStatusEnum._20.getCode().equals(tempStorageSettle.getStatus())) {
            throw new BusinessRuntimeException("已审核无法再次审核");
        }

        tempStorageSettle = new TCostStorageSettlePO();
        tempStorageSettle.setId(storageSettleId);
        tempStorageSettle.setStatus(StorageSettleStatusEnum._20.getCode());
        mapper.review(tempStorageSettle);

        TCostStorageSettleDTO storageSettle = mapper.getStorageSettle(storageSettleId);
        TBusHandoverlistPO handoverlist = mapper.getHandoverlist(storageSettle.getHandoverlistId());

        TCostStatementPO statement = new TCostStatementPO();
        statement.setId(snowflake.nextId());
        statement.setCompanyId(storageSettle.getCompanyId());
        statement.setCompanyName(storageSettle.getCompanyName());
        statement.setStatementNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.STATEMENT_NO, null));
        statement.setCustomerId(storageSettle.getCargoOwnerId());
        statement.setCustomerName(storageSettle.getCargoOwnerName());
        statement.setType(HandoverlistTypeEnum._50.getCode());
        if (handoverlist != null) statement.setTrustId(handoverlist.getTrustId());
        if (handoverlist != null) statement.setTrustCargoId(handoverlist.getTrustCargoId());
        if (handoverlist != null) statement.setHandoverlistId(handoverlist.getId());
        statement.setShipvoyageId(storageSettle.getShipvoyageId());
        statement.setShipvoyageItemId(storageSettle.getShipvoyageItemId());
        if (handoverlist != null) statement.setCargoCode(handoverlist.getCargoCode());
        if (handoverlist != null) statement.setCargoName(handoverlist.getCargoName());
        if (handoverlist != null) statement.setTradeType(handoverlist.getTradeType());
        statement.setSettlementDate(storageSettle.getCreateTime());
        statement.setStatus(StatementStatusEnum._30.getCode());
        statement.setIsFinal(IsFinalEnum.TRUE.getCode());

        TBusContractRatePO contractRate = mapper.getContractRate(storageSettle.getContractRateId());

        TCostStatementDetailPO detail = new TCostStatementDetailPO();
        detail.setId(snowflake.nextId());
        detail.setStatement(statement.getId());
        detail.setContractId(storageSettle.getContractId());
        detail.setRateItemCode(contractRate.getRateItemCode());
        detail.setRateItemName(contractRate.getRateItemName());
        detail.setServiceContentId(contractRate.getServiceContentId());
        detail.setServiceContentName(contractRate.getServiceContentName());
        detail.setProcessCode(contractRate.getProcessCode());
        detail.setProcessName(contractRate.getProcessName());
        detail.setRate(storageSettle.getRate());
        detail.setUnitCode(contractRate.getUnitCode());
        detail.setUnitName(contractRate.getUnitName());
        detail.setNumber(storageSettle.getAmount().divide(storageSettle.getRate(), 2, RoundingMode.HALF_UP));
        detail.setAmount(storageSettle.getAmount());
        detail.setInvoiceNumber(ZERO);
        detail.setInvoiceAmount(ZERO);
        detail.setTax(storageSettle.getTax());
        detail.setTaxAmount(
                detail.getAmount().divide(
                        BigDecimal.ONE.add(
                                detail.getTax().divide(
                                        BigDecimal.valueOf(100),
                                        4, RoundingMode.HALF_UP
                                )
                        ), 100, RoundingMode.HALF_UP
                ).multiply(
                        detail.getTax().divide(
                                BigDecimal.valueOf(100),
                                4, RoundingMode.HALF_UP
                        )
                ).setScale(2, RoundingMode.HALF_UP)
        );
        detail.setBusinessId(storageSettle.getId());
        detail.setRateId(storageSettle.getContractRateId());

        mapper.insertStatement(statement);
        mapper.insertStatementDetail(detail);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void cancelReview(Long storageSettleId) {
        TCostStorageSettlePO storageSettle = mapper.getStorageSettle(storageSettleId);
        if (StorageSettleStatusEnum._10.getCode().equals(storageSettle.getStatus())) {
            throw new BusinessRuntimeException("未审核无法销审");
        }
        TCostStatementPO statement = mapper.getStatement(storageSettleId);
        if (!StatementStatusEnum._30.getCode().equals(statement.getStatus())) {
            throw new BusinessRuntimeException("结算单已开票，无法销审");
        }

        storageSettle = new TCostStorageSettlePO();
        storageSettle.setId(storageSettleId);
        storageSettle.setStatus(StorageSettleStatusEnum._10.getCode());
        mapper.cancelReview(storageSettle);

        mapper.deleteCostStatement(statement.getId());
        mapper.deleteCostStatementDetail(statement.getId());
    }
}
