package com.yy.ppm.statement.service.impl.storageSettle;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.enums.CommonEnum;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtil;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import com.yy.ppm.business.bean.po.TBusContractRatePO;
import com.yy.ppm.common.enums.*;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.statement.bean.dto.ConfirmForMiscAndStorageDTO;
import com.yy.ppm.statement.bean.dto.CostBillDtoSheetTemplate;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import com.yy.ppm.statement.bean.dto.storageSettle.*;
import com.yy.ppm.statement.bean.po.*;
import com.yy.ppm.statement.mapper.storageSettle.StorageSettleJGSGMapper;
import com.yy.ppm.statement.mapper.storageSettle.StorageSettleJGZCMapper;
import com.yy.ppm.statement.mapper.storageSettle.StorageSettleMapper;
import com.yy.ppm.statement.mapper.storageSettle.StorageSettleXCMapper;
import com.yy.ppm.statement.service.storageSettle.StorageSettleService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yy.common.util.DateUtil.*;
import static java.math.BigDecimal.ZERO;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-24 14:13
 */
@Service
public class StorageSettleServiceImpl implements StorageSettleService {

    @Autowired
    private StorageSettleMapper mapper;

    @Autowired
    private StorageSettleXCMapper xcMapper;

    @Autowired
    private StorageSettleJGZCMapper jgzcMapper;

    @Autowired
    private StorageSettleJGSGMapper jgsgMapper;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private SysFileService sysFileService;
    @Resource
    private SecurityUtils securityUtils;

    @Autowired
    private CommonService commonService;

    @Override
    public Pages<TBusHandoverlistDTO> listHandoverlist(PageParameter parameter, TBusHandoverlistQueryDTO query) {
        return PageHelperUtils.limit(parameter, () -> {
            return mapper.listHandoverlist(query);
        });
    }

    private StorageFlowDirectionEnum getFlowDirection(Long handoverlistId) {
        TBusHandoverlistPO handoverlist = mapper.getHandoverlist(handoverlistId);
        if (BusHandoverlistTypeEnum.ZHUANGXIECHUAN.getCode().equals(handoverlist.getType())) {
            if (handoverlist.getLoadUnload().equals("卸")) {
                return StorageFlowDirectionEnum.卸船;
            } else {
                return StorageFlowDirectionEnum.集港装船;
            }
        } else {
            return StorageFlowDirectionEnum.集港疏港;
        }
    }

    @Override
    public List<TCostStorageSettleDetailDTO> listDetail(Long handoverlistId) {
        List<TCostStorageSettleDetailDTO> details = listDetailContainsHistory(handoverlistId);
        return filterHistoryDetail(handoverlistId, details);
    }

    private List<TCostStorageSettleDetailDTO> listDetailContainsHistory(Long handoverlistId) {
        StorageFlowDirectionEnum flowDirection = getFlowDirection(handoverlistId);
        switch (flowDirection) {
            case 卸船:
                return listDetailXC(handoverlistId);
            case 集港装船:
                return listDetailJGZC(handoverlistId);
            default:
                return listDetailJGSG(handoverlistId);
        }
    }

    private List<TCostStorageSettleDetailDTO> filterHistoryDetail(Long handoverlistId, List<TCostStorageSettleDetailDTO> details) {
        TBusHandoverlistPO handoverlist = mapper.getHandoverlist(handoverlistId);
        List<TCostStorageSettleDetailDTO> _details = mapper.listStorageSettleDetail(handoverlist.getCargoInfoId(), null, null);
        return details.stream().filter(v1 -> _details.stream().noneMatch(v2 -> v1.getDate().equals(v2.getDate()))).collect(Collectors.toList());
    }

    private Pair<Map<LocalDate, BigDecimal>, Map<LocalDate, BigDecimal>> getTransferInOutTons(Long handoverlistId) {
        TBusCargoInfoPO cargoInfo = mapper.getCargoInfo(handoverlistId);
        if (cargoInfo == null) {
            throw new BusinessRuntimeException("找不到该交接清单关联的票货");
        }
        Map<LocalDate, BigDecimal> transferInTons = new HashMap<>();
        Map<LocalDate, BigDecimal> transferOutTons = new HashMap<>();
        if (cargoInfo.getRootId() != null) {
            transferInTons.put(date2LocalDate(cargoInfo.getTransferDate()), Optional.ofNullable(cargoInfo.getTon()).orElse(ZERO));
        }
        List<TBusCargoInfoPO> cargoInfos = mapper.listTransferOutCargoInfo(cargoInfo.getId());
        cargoInfos.stream().collect(Collectors.groupingBy(TBusCargoInfoPO::getTransferDate)).forEach((k, v) -> {
            transferOutTons.put(date2LocalDate(k), v.stream().map(v1 -> Optional.ofNullable(v1.getTon()).orElse(ZERO)).reduce(ZERO, BigDecimal::add));
        });
        return Pair.of(transferInTons, transferOutTons);
    }

    private Map<LocalDate, BigDecimal> getMixOutTons(Long handoverlistId) {
        TBusCargoInfoPO cargoInfo = mapper.getCargoInfo(handoverlistId);
        List<Map<String, Object>> mixOutWeights = mapper.listMixOutWeight(cargoInfo.getId());
        return mixOutWeights.stream()
                .map(v1 -> Pair.of(date2LocalDate((Date) v1.get("mixDate")), (BigDecimal) v1.get("mixWeight")))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    private List<TCostStorageSettleDetailDTO> listDetailXC(Long handoverlistId) {
        TDisShipvoyageItemPO shipvoyageItem = xcMapper.getShipvoyageItem(handoverlistId);
        if (shipvoyageItem.getWorkStartTime() == null) {
            throw new BusinessRuntimeException("船舶航次尚未开工");
        }

        List<VWeightInfo> weightInfos = xcMapper.listWeightInfo(handoverlistId);
        TBusHandoverlistPO handoverlist = mapper.getHandoverlist(handoverlistId);
        List<Map<String, Object>> loadShipvoyageItems = xcMapper.listLoadShipvoyageItem(handoverlist.getCargoInfoId());
        Pair<Map<LocalDate, BigDecimal>, Map<LocalDate, BigDecimal>> transferInOutTons = getTransferInOutTons(handoverlistId);
        if (weightInfos.isEmpty() && loadShipvoyageItems.isEmpty() && transferInOutTons.getRight().isEmpty()) {
            throw new BusinessRuntimeException("找不到过磅数据和装船交接清单和货转记录");
        }

        LocalDate beginDate = date2LocalDate(shipvoyageItem.getWorkStartTime());
        Optional<Map.Entry<LocalDate, BigDecimal>> optional = transferInOutTons.getRight().entrySet().stream().max(Map.Entry.comparingByKey());
        LocalDate temp;
        if (optional.isPresent()) {
            temp = optional.get().getKey();
        } else {
            temp = date2LocalDate(new Date(0));
        }
        LocalDate endDate = max(
                date2LocalDate(Optional.ofNullable(weightInfos.stream().max(Comparator.comparing(VWeightInfo::getWeighOutDt)).orElse(new VWeightInfo()).getWeighOutDt()).orElse(new Date(0))),
                date2LocalDate(Optional.ofNullable((Date) loadShipvoyageItems.stream().max(Comparator.comparing(v1 -> (Date) v1.get("workEndTime"))).orElse(Collections.emptyMap()).get("workEndTime")).orElse(new Date(0))),
                temp
        );
        List<LocalDate> dates = new ArrayList<>();
        LocalDate tempDate = beginDate.minusDays(1);
        while (!(tempDate = tempDate.plusDays(1)).isAfter(endDate)) {
            dates.add(tempDate);
        }

        Map<LocalDate, BigDecimal> mixOutTons = getMixOutTons(handoverlistId);

        BigDecimal firstdayInTon = weightInfos.stream().map(VWeightInfo::getWeightGoods).reduce(ZERO, BigDecimal::add).add(loadShipvoyageItems.stream().map(v1 -> new BigDecimal(String.valueOf(v1.get("ton")))).reduce(ZERO, BigDecimal::add));
        Map<LocalDate, BigDecimal> inTons = dates.stream()
                .map(v1 -> {
                    BigDecimal everydayInTon = ZERO;
                    if (v1.equals(beginDate)) {
                        everydayInTon = firstdayInTon
                                .add(transferInOutTons.getRight().values().stream().reduce(ZERO, BigDecimal::add))
                                .add(mixOutTons.values().stream().reduce(ZERO, BigDecimal::add));
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
            everydayOutTon = everydayOutTon
                    .add(Optional.ofNullable(transferInOutTons.getRight().get(v1)).orElse(ZERO))
                    .add(Optional.ofNullable(mixOutTons.get(v1)).orElse(ZERO));
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

    private List<TCostStorageSettleDetailDTO> listDetailJGZC(Long handoverlistId) {
        List<VWeightInfo> weightInfos = jgzcMapper.listWeightInfo(handoverlistId);
        if (weightInfos.isEmpty()) {
            throw new BusinessRuntimeException("找不到过磅数据");
        }
        VWeightInfo earliestWeightInfo = weightInfos.stream().min(Comparator.comparing(VWeightInfo::getWeighOutDt)).orElseThrow(null);

        TDisShipvoyageItemPO shipvoyageItem = jgzcMapper.getShipvoyageItem(handoverlistId);
        if (shipvoyageItem.getWorkEndTime() == null) {
            throw new BusinessRuntimeException("船舶航次尚未完工");
        }

        LocalDate beginDate = date2LocalDate(earliestWeightInfo.getWeighOutDt());
        LocalDate endDate = date2LocalDate(shipvoyageItem.getWorkEndTime());
        List<LocalDate> dates = new ArrayList<>();
        LocalDate tempDate = beginDate.minusDays(1);
        while (!(tempDate = tempDate.plusDays(1)).isAfter(endDate)) {
            dates.add(tempDate);
        }

        Pair<Map<LocalDate, BigDecimal>, Map<LocalDate, BigDecimal>> transferInOutTons = getTransferInOutTons(handoverlistId);
        Map<LocalDate, BigDecimal> mixOutTons = getMixOutTons(handoverlistId);

        Map<LocalDate, BigDecimal> inTons = dates.stream().map(v1 -> {
            BigDecimal everydayInTon = weightInfos.stream()
                    .filter(v2 -> date2LocalDate(v2.getWeighOutDt()).equals(v1))
                    .map(VWeightInfo::getWeightGoods)
                    .reduce(ZERO, BigDecimal::add);
            everydayInTon = everydayInTon.add(Optional.ofNullable(transferInOutTons.getLeft().get(v1)).orElse(ZERO));
            return Pair.of(v1, everydayInTon);
        }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        TBusHandoverlistPO handoverlist = mapper.getHandoverlist(handoverlistId);
        List<Map<String, Object>> shipvoyageItems = jgzcMapper.listShipvoyageItem(handoverlist.getCargoInfoId());
        Map<LocalDate, BigDecimal> outTons = dates.stream()
                .map(v1 -> {
                    BigDecimal everydayOutTon = shipvoyageItems.stream()
                            .filter(v2 -> date2LocalDate((Date) v2.get("workEndTime")).equals(v1))
                            .map(v2 -> (BigDecimal) v2.get("ton"))
                            .reduce(ZERO, BigDecimal::add);
                    everydayOutTon = everydayOutTon.add(Optional.ofNullable(transferInOutTons.getRight().get(v1)).orElse(ZERO));
                    everydayOutTon = everydayOutTon.add(Optional.ofNullable(mixOutTons.get(v1)).orElse(ZERO));
                    return Pair.of(v1, everydayOutTon);
                })
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

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

    private List<TCostStorageSettleDetailDTO> listDetailJGSG(Long handoverlistId) {
        List<VWeightInfo> JGWeightInfos = jgsgMapper.listJGWeightInfo(handoverlistId);
        if (JGWeightInfos.isEmpty()) {
            throw new BusinessRuntimeException("找不到集港过磅数据");
        }
        VWeightInfo earliestUnloadWeightInfo = JGWeightInfos.stream().min(Comparator.comparing(VWeightInfo::getWeighOutDt)).orElseThrow(null);

        List<VWeightInfo> SGWeightInfos = jgsgMapper.listSGWeightInfo(handoverlistId);
        if (SGWeightInfos.isEmpty()) {
            throw new BusinessRuntimeException("找不到疏港过磅数据");
        }
        VWeightInfo latestLoadWeightInfo = SGWeightInfos.stream().max(Comparator.comparing(VWeightInfo::getWeighOutDt)).orElseThrow(null);

        LocalDate beginDate = date2LocalDate(earliestUnloadWeightInfo.getWeighOutDt());
        LocalDate endDate = date2LocalDate(latestLoadWeightInfo.getWeighOutDt());
        List<LocalDate> dates = new ArrayList<>();
        LocalDate tempDate = beginDate.minusDays(1);
        while (!(tempDate = tempDate.plusDays(1)).isAfter(endDate)) {
            dates.add(tempDate);
        }

        Pair<Map<LocalDate, BigDecimal>, Map<LocalDate, BigDecimal>> transferInOutTons = getTransferInOutTons(handoverlistId);
        Map<LocalDate, BigDecimal> mixOutTons = getMixOutTons(handoverlistId);

        Map<LocalDate, BigDecimal> inTons = dates.stream().map(v1 -> {
            BigDecimal everydayInTon = JGWeightInfos.stream()
                    .filter(v2 -> date2LocalDate(v2.getWeighOutDt()).equals(v1))
                    .map(VWeightInfo::getWeightGoods)
                    .reduce(ZERO, BigDecimal::add);
            everydayInTon = everydayInTon.add(Optional.ofNullable(transferInOutTons.getLeft().get(v1)).orElse(ZERO));
            return Pair.of(v1, everydayInTon);
        }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        Map<LocalDate, BigDecimal> outTons = dates.stream().map(v1 -> {
            BigDecimal everydayOutTon = SGWeightInfos.stream()
                    .filter(v2 -> date2LocalDate(v2.getWeighOutDt()).equals(v1))
                    .map(VWeightInfo::getWeightGoods)
                    .reduce(ZERO, BigDecimal::add);
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

    @Override
    public List<Map<String, Object>> listContract(Long handoverlistId, Date date) {
        return mapper.listContract(handoverlistId, date);
    }

    private Pair<Boolean, Integer> getReduceDays(Long cargoInfoId, LocalDate beginDate) {
        TBusCargoInfoPO cargoInfo = mapper.getCargoInfo(cargoInfoId);
        TBusStackFeeReducePO stackFeeReduce = mapper.getStackFeeReduce(cargoInfo.getId());
        boolean hasReduce = stackFeeReduce != null;
        if (hasReduce) {
            long reduceDays = 0;
            if (ReduceTypeEnum._1.getCode().equals(stackFeeReduce.getReduceType())) {
                reduceDays = stackFeeReduce.getReduceDays();
            }
            if (ReduceTypeEnum._2.getCode().equals(stackFeeReduce.getReduceType())) {
                reduceDays = Math.max(DateUtil.getDateDifference(beginDate, date2LocalDate(stackFeeReduce.getReduceEndDate())), 0);
            }
            return Pair.of(true, (int) reduceDays);
        }
        return Pair.of(false, null);
    }

    @Override
    public List<TCostStorageSettleDetailDTO> listDetailWithContract(Long handoverlistId, Long contractRateId, String isUseReduce) {
        TBusContractRatePO contractRate = mapper.getContractRate(contractRateId);

        List<TCostStorageSettleDetailDTO> details = listDetailContainsHistory(handoverlistId);

        Map<LocalDate, BigDecimal> inTons = details.stream()
                .map(v1 -> Pair.of(date2LocalDate(v1.getDate()), v1.getInTon()))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (prev, next) -> next, LinkedHashMap::new));

        Pair<Boolean, Integer> reduceDays = getReduceDays(handoverlistId, date2LocalDate(details.get(0).getDate()));

        List<TCostStorageSettleDetailDTO> finalDetails = details;
        details.forEach(v1 -> {
            BigDecimal inTon = finalDetails.stream()
                    .filter(v2 -> !date2LocalDate(v1.getDate()).minusDays(CommonEnum.YesNoMode.YES.getCode().equals(isUseReduce) && reduceDays.getLeft() ? reduceDays.getRight() : Optional.ofNullable(contractRate.getFreeStorageDays()).orElse(0)).isBefore(date2LocalDate(v2.getDate())))
                    .map(TCostStorageSettleDetailPO::getInTon)
                    .reduce(ZERO, BigDecimal::add);
            BigDecimal outTon = finalDetails.stream()
                    .filter(v2 -> !date2LocalDate(v1.getDate()).isBefore(date2LocalDate(v2.getDate())))
                    .map(TCostStorageSettleDetailPO::getOutTon)
                    .reduce(ZERO, BigDecimal::add);
            BigDecimal billableTon = inTon.subtract(outTon);
            if (billableTon.compareTo(ZERO) < 0) {
                List<Map.Entry<LocalDate, BigDecimal>> tempInTons = inTons.entrySet().stream()
                        .filter(v2 -> date2LocalDate(v1.getDate()).minusDays(CommonEnum.YesNoMode.YES.getCode().equals(isUseReduce) && reduceDays.getLeft() ? reduceDays.getRight() : Optional.ofNullable(contractRate.getFreeStorageDays()).orElse(0)).isBefore(v2.getKey())
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
        details = filterHistoryDetail(handoverlistId, details);
        return details;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void settle(TCostStorageSettleDTO storageSettle) {
        List<TCostStorageSettleDTO> storageSettles = mapper.listStorageSettle(storageSettle.getHandoverlistId());
        if (!storageSettles.isEmpty()) {
            throw new BusinessRuntimeException("当前仅支持结算一次");
        }

        StorageFlowDirectionEnum flowDirection = getFlowDirection(storageSettle.getHandoverlistId());
        if (StorageFlowDirectionEnum.集港装船 == flowDirection) {
            List<Map<String, Object>> shipvoyageItems = jgzcMapper.listShipvoyageItem(storageSettle.getCargoInfoId());
            if (shipvoyageItems.size() > 1) {
                List<TCostStorageSettlePO> _storageSettles = jgzcMapper.listStorageSettle(storageSettle.getCargoInfoId());
                for (Map<String, Object> loadShipvoyageItem : shipvoyageItems) {
                    Long shipvoyageItemId = Long.valueOf(String.valueOf(loadShipvoyageItem.get("shipvoyageItemId")));
                    boolean bool = storageSettle.getShipvoyageItemId().equals(shipvoyageItemId);
                    if (!bool) {
                        bool = _storageSettles.stream().noneMatch(v1 -> shipvoyageItemId.equals(v1.getShipvoyageItemId()));
                        if (bool) {
                            throw new BusinessRuntimeException(
                                    String.format("当前交接清单票货存在多个装船航次，结算日期存在重叠，请先结算先完工的【%s_%s】",
                                            loadShipvoyageItem.get("shipName"),
                                            loadShipvoyageItem.get("voyage")));
                        }
                    } else {
                        break;
                    }
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
    public List<TCostStorageSettleDTO> listStorageSettle(Long handoverlistId) {
        return mapper.listStorageSettle(handoverlistId);
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
        statement.setTrustId(handoverlist.getTrustId());
        statement.setTrustCargoId(handoverlist.getTrustCargoId());
        statement.setHandoverlistId(handoverlist.getId());
        statement.setShipvoyageId(storageSettle.getShipvoyageId());
        statement.setShipvoyageItemId(storageSettle.getShipvoyageItemId());
        statement.setCargoCode(handoverlist.getCargoCode());
        statement.setCargoName(handoverlist.getCargoName());
        statement.setTradeType(handoverlist.getTradeType());
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

    @Override
    public List<TCostStorageSettleDTO> listStorageSettleForConfirm(Long handoverlistId) {
        return mapper.listStorageSettleForConfirm(handoverlistId);
    }

    @Override
    public void saveConfirmFile(ConfirmForMiscAndStorageDTO dto) {
        if (CollectionUtils.isEmpty(dto.getFileIds())) {
            throw new BusinessRuntimeException("请先上传文件在进行保存");
        }
        dto.getIds().forEach(o -> {
            sysFileService.saveFileBusRelation(dto.getFileIds(), o);
        });
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void confirm(ConfirmForMiscAndStorageDTO dto) {
        //杂项

        if (CollectionUtils.isEmpty(dto.getIds())) {
            throw new BusinessRuntimeException("请先选中数据再进行保存");
        }

        if (dto == null) {
            throw new BusinessRuntimeException("请选择操作得数据");
        }
        if (CollectionUtils.isEmpty(dto.getIds())) {
            throw new BusinessRuntimeException("结算单id不能为空");
        }
        dto.getIds().forEach(tmpId -> {
            if (tmpId == null) {
                throw new BusinessRuntimeException("还未进行结算!");
            }

            TCostStatementPO costStatement = mapper.getStatementById(tmpId);
            if (!StatementStatusEnum._30.getCode().equals(costStatement.getStatus())) {
                throw new BusinessRuntimeException("商务确认失败，状态非计费审核");
            }
            TCostStatementPO tCostStatementPO = new TCostStatementPO();
            tCostStatementPO.setConfirmBy(securityUtils.getLoginUserId());
            tCostStatementPO.setConfirmByName(securityUtils.getLoginUserName());
            tCostStatementPO.setConfirmTime(new Date());
            tCostStatementPO.setStatus(StatementStatusEnum._31.getCode());
            tCostStatementPO.setReceiptRemark(null);
            tCostStatementPO.setTaxInvoiceCode(dto.getTaxInvoiceCode());
            tCostStatementPO.setTaxInvoiceName(dto.getTaxInvoiceName());
            tCostStatementPO.setId(tmpId);
            mapper.updateCostStatement(tCostStatementPO);
        });

    }


    @Override
    public void cancelConfirm(ConfirmForMiscAndStorageDTO dto) {

        if (dto == null) {
            throw new BusinessRuntimeException("请选择操作得数据");
        }
        if (CollectionUtils.isEmpty(dto.getIds())) {
            throw new BusinessRuntimeException("结算单id不能为空");
        }

        dto.getIds().forEach(tmpId -> {

            TCostStatementPO costStatement = mapper.getStatementById(tmpId);
            if (!StatementStatusEnum._31.getCode().equals(costStatement.getStatus())) {
                throw new BusinessRuntimeException("操作失败，当前状态非回执已确认");
            }

            costStatement.setReceiptRemark(null);
            costStatement.setTaxInvoiceName(null);
            costStatement.setTaxInvoiceCode(null);
            costStatement.setStatus(StatementStatusEnum._30.getCode());
            costStatement.setConfirmTime(null);
            costStatement.setConfirmByName(null);
            costStatement.setConfirmBy(null);
            mapper.updateCostStatement(costStatement);
        });

    }

    /**
     * 堆存费 打印账单
     *
     * @param ids
     * @return
     */
    @Override
    public CostBillDtoSheetTemplate printFeeList(List<Long> ids) {
        List<TBusHandoverlistDTO> dataList = mapper.getHandoverListByIds(ids);
        if (CollectionUtils.isEmpty(dataList)) {
            throw new BusinessRuntimeException("没有找到对应的堆存费信息");
        }
        Map<Long, TBusHandoverlistDTO> dtoMap = dataList.stream().collect(Collectors.toMap(TBusHandoverlistDTO::getId, Function.identity(), (k1, k2) -> k2));

        CostBillDtoSheetTemplate result = new CostBillDtoSheetTemplate();
        result.setTin(dataList.get(0).getTin());
        result.setBank(dataList.get(0).getBank());
        result.setBankAccount(dataList.get(0).getBankAccount());
        result.setContactNumber(dataList.get(0).getContactNumber());
        result.setAddress(dataList.get(0).getAddress());
        result.setAmount(ZERO);
        result.setNumberCount(ZERO);
        result.setCustomerName(dataList.get(0).getCargoOwnerName());
        List<TCostStatementDetailDTO> tmpCountStatements = new ArrayList<>(3);

        List<TCostStatementDetailDTO> dataDetailList = new ArrayList<>();
        StringBuilder statementName = new StringBuilder();
        StringBuilder reviewName = new StringBuilder();
        StringBuilder confirmName = new StringBuilder();
        StringBuilder contractNo = new StringBuilder();
        StringBuilder settlementBasisName = new StringBuilder();
        dataDetailList = mapper.getStorageSettleStatementList(ids);

        if (!CollectionUtils.isEmpty(dataDetailList)) {
            result.setReviewTime(DateUtils.formatDate(dataDetailList.get(0).getReviewTime(), CommonEnum.DateFormatType.E_1.getCode()));
            dataDetailList = dataDetailList.stream().filter(o -> (o.getNumber() != null) && BigDecimal.ZERO.compareTo(o.getNumber()) != 0).collect(Collectors.toList());

        }
        if (!CollectionUtils.isEmpty(dataDetailList)) {
            TCostStatementDetailDTO detail = new TCostStatementDetailDTO();
            detail.setFeeName("小计（库场使用费）");
            detail.setAmount(BigDecimal.ZERO);
            detail.setNumber(BigDecimal.ZERO);
            dataDetailList.forEach(o -> {
                detail.setAmount(detail.getAmount().add(o.getAmount()));
                detail.setNumber(detail.getNumber().add(o.getNumber()));
            });
            dataDetailList.add(detail);
            tmpCountStatements.add(detail);
        }

        //合同编号
        if (contractNo.length() > 1) {
            result.setContractNo(contractNo.substring(0, contractNo.length() - 1));
        }
        result.setConfirmByName(confirmName.length() > 1 ? confirmName.substring(0, confirmName.length() - 1) : "");
        result.setReviewByName(reviewName.length() > 1 ? reviewName.substring(0, reviewName.length() - 1) : "");
        result.setStatementByName(statementName.length() > 1 ? statementName.substring(0, statementName.length() - 1) : "");

        //包干费
        List<TCostStatementDetailDTO> BGFList = mapper.getBGFeeListStatement(ids);
        if (!CollectionUtils.isEmpty(BGFList)) {
            BGFList = BGFList.stream().filter(o -> (o.getNumber() != null) && BigDecimal.ZERO.compareTo(o.getNumber()) != 0).collect(Collectors.toList());

        }
        if (!CollectionUtils.isEmpty(BGFList)) {
            if (CollectionUtils.isEmpty(dataDetailList)) {
                dataDetailList = BGFList;
            } else {
                dataDetailList.addAll(BGFList);

            }

            TCostStatementDetailDTO BGFDetail = new TCostStatementDetailDTO();
            BGFDetail.setFeeName("小计（包干费）");
            BGFDetail.setAmount(BigDecimal.ZERO);
            BGFDetail.setNumber(BigDecimal.ZERO);
            BGFList.forEach(o -> {
                BGFDetail.setAmount(BGFDetail.getAmount().add(o.getAmount()));
                BGFDetail.setNumber(BGFDetail.getNumber().add(o.getNumber()));
            });
            dataDetailList.add(BGFDetail);
            tmpCountStatements.add(BGFDetail);

        }

        //杂项
        List<Long> cargoInfoIdList = dataList.stream().map(TBusHandoverlistDTO::getCargoInfoId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(cargoInfoIdList)) {
            List<TCostStatementDetailDTO> MISCList = mapper.getMISCStatementList(cargoInfoIdList);
            if (!CollectionUtils.isEmpty(MISCList)) {
                if (CollectionUtils.isEmpty(dataDetailList)) {
                    dataDetailList = MISCList;
                } else {
                    dataDetailList.addAll(MISCList);

                }
                TCostStatementDetailDTO MISCDetail = new TCostStatementDetailDTO();
                MISCDetail.setFeeName("小计（其他）");
                MISCDetail.setAmount(BigDecimal.ZERO);
                MISCDetail.setNumber(BigDecimal.ZERO);
                MISCList.forEach(o -> {
                    MISCDetail.setAmount(MISCDetail.getAmount().add(o.getAmount()));
                    MISCDetail.setNumber(MISCDetail.getNumber().add(o.getNumber()));
                });
                dataDetailList.add(MISCDetail);
                tmpCountStatements.add(MISCDetail);

            }
        }

        if (CollectionUtils.isEmpty(dataDetailList)) {
            throw new BusinessRuntimeException("没有要打印的账单,可能还没进行结算");
        }
        if (!CollectionUtils.isEmpty(dataDetailList)) {
            dataDetailList.forEach(o -> {
                if (!org.apache.axis.utils.StringUtils.isEmpty(o.getContactNo()) && !contractNo.toString().contains(o.getContactNo())) {
                    contractNo.append(o.getContactNo()).append("_");
                }
                if (!org.apache.axis.utils.StringUtils.isEmpty(o.getReviewName()) && !reviewName.toString().contains(o.getReviewName())) {
                    reviewName.append(o.getReviewName()).append("/");
                }
                if (!org.apache.axis.utils.StringUtils.isEmpty(o.getStatementName()) && !statementName.toString().contains(o.getStatementName())) {
                    statementName.append(o.getStatementName()).append("/");
                }
                if (!org.apache.axis.utils.StringUtils.isEmpty(o.getConfirmName()) && !confirmName.toString().contains(o.getConfirmName())) {
                    confirmName.append(o.getConfirmName()).append("/");
                }
                if (!org.apache.axis.utils.StringUtils.isEmpty(o.getSettlementBasisName()) && !settlementBasisName.toString().contains(o.getSettlementBasisName())) {
                    settlementBasisName.append(o.getSettlementBasisName()).append("/");
                }
            });
        }
        tmpCountStatements.forEach(o -> {
            result.setAmount(result.getAmount().add(o.getAmount()));
            result.setNumberCount(result.getNumberCount().add(o.getNumber()));
        });

        result.setCostNumberName("结算天数");
        result.setDetailList(dataDetailList);
        result.setSheetName("库场使用费账单");

        result.setConfirmByName(confirmName.length() > 1 ? confirmName.substring(0, confirmName.length() - 1) : "");
        result.setReviewByName(reviewName.length() > 1 ? reviewName.substring(0, reviewName.length() - 1) : "");
        result.setStatementByName(statementName.length() > 1 ? statementName.substring(0, statementName.length() - 1) : "");
        result.setContractNo(contractNo.length() > 1 ? contractNo.substring(0, contractNo.length() - 1) : "");
        result.setSettlementBasisName(settlementBasisName.length() > 1 ? settlementBasisName.substring(0, settlementBasisName.length() - 1) : "");
        result.setCompanyName(dataList.get(0).getCompanyName());

        return result;
    }
}
