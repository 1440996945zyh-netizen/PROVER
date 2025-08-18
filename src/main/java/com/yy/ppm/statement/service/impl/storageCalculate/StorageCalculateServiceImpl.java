package com.yy.ppm.statement.service.impl.storageCalculate;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.enums.CommonEnum;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtil;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import com.yy.ppm.business.bean.po.TBusContractRatePO;
import com.yy.ppm.common.enums.ReduceTypeEnum;
import com.yy.ppm.common.enums.StorageSourceEnum;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.statement.bean.dto.storageCalculate.*;
import com.yy.ppm.statement.bean.po.TBusHandoverlistPO;
import com.yy.ppm.statement.bean.po.TBusStackFeeReducePO;
import com.yy.ppm.statement.bean.po.TCostStorageSettleDetailPO;
import com.yy.ppm.statement.bean.po.VWeightInfoPO;
import com.yy.ppm.statement.mapper.storageCalculate.StorageCalculateJGMapper;
import com.yy.ppm.statement.mapper.storageCalculate.StorageCalculateMapper;
import com.yy.ppm.statement.mapper.storageCalculate.StorageCalculateXCMapper;
import com.yy.ppm.statement.service.storageCalculate.StorageCalculateService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.yy.common.util.DateUtil.date2LocalDate;
import static com.yy.common.util.DateUtil.localDate2Date;
import static com.yy.ppm.common.enums.StorageSourceEnum.卸船;
import static java.math.BigDecimal.ZERO;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-24 14:13
 */
@Service
public class StorageCalculateServiceImpl implements StorageCalculateService {

    @Autowired
    private StorageCalculateMapper mapper;

    @Autowired
    private StorageCalculateXCMapper xcMapper;

    @Autowired
    private StorageCalculateJGMapper jgMapper;

    @Autowired
    private Snowflake snowflake;

    @Override
    public Pages<TBusCargoInfoDTO> listCargoInfo(PageParameter parameter, TBusCargoInfoQueryDTO query) {
        return PageHelperUtils.limit(parameter, () -> mapper.listCargoInfo(query));
    }

    private StorageSourceEnum getSource(Long cargoInfoId) {
        TBusCargoInfoPO cargoInfo = mapper.getCargoInfo(cargoInfoId);
        if (cargoInfo.getShipvoyageId() != null) {
            return StorageSourceEnum.卸船;
        } else {
            return StorageSourceEnum.集港;
        }
    }

    @Override
    public List<TCostStorageSettleDetailDTO> listDetail(Long cargoInfoId) {
        return listDetailContainsHistory(cargoInfoId);
    }

    private List<TCostStorageSettleDetailDTO> listDetailContainsHistory(Long cargoInfoId) {
        StorageSourceEnum source = getSource(cargoInfoId);
        if (source == 卸船) {
            return listDetailXC(cargoInfoId);
        }
        return listDetailJG(cargoInfoId);
    }

    private Pair<Map<LocalDate, BigDecimal>, Map<LocalDate, BigDecimal>> getTransferInOutTons(Long cargoInfoId) {
        TBusCargoInfoPO cargoInfo = mapper.getCargoInfo(cargoInfoId);
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

    private Map<LocalDate, BigDecimal> getMixOutTons(Long cargoInfoId) {
        TBusCargoInfoPO cargoInfo = mapper.getCargoInfo(cargoInfoId);
        List<Map<String, Object>> mixOutWeights = mapper.listMixOutWeight(cargoInfo.getId());
        return mixOutWeights.stream()
                .map(v1 -> Pair.of(date2LocalDate((Date) v1.get("mixDate")), (BigDecimal) v1.get("mixWeight")))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    private List<TCostStorageSettleDetailDTO> listDetailXC(Long cargoInfoId) {
        TDisShipvoyageItemPO shipvoyageItem = xcMapper.getShipvoyageItem(cargoInfoId);
        if (shipvoyageItem.getWorkStartTime() == null) {
            throw new BusinessRuntimeException("船舶航次尚未开工");
        }
        List<TBusHandoverlistPO> handoverlists = xcMapper.listHandoverlist(cargoInfoId);

        List<VWeightInfoPO> weightInfos = xcMapper.listWeightInfo(cargoInfoId);
        List<Map<String, Object>> loadShipvoyageItems = xcMapper.listLoadShipvoyageItem(cargoInfoId);

        LocalDate beginDate = date2LocalDate(shipvoyageItem.getWorkStartTime());
        LocalDate endDate = LocalDate.now();
        List<LocalDate> dates = new ArrayList<>();
        LocalDate tempDate = beginDate.minusDays(1);
        while (!(tempDate = tempDate.plusDays(1)).isAfter(endDate)) {
            dates.add(tempDate);
        }

        Pair<Map<LocalDate, BigDecimal>, Map<LocalDate, BigDecimal>> transferInOutTons = getTransferInOutTons(cargoInfoId);
        Map<LocalDate, BigDecimal> mixOutTons = getMixOutTons(cargoInfoId);

        BigDecimal firstdayInTon = handoverlists.stream().map(TBusHandoverlistPO::getTon).reduce(ZERO, BigDecimal::add);
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
                    .map(VWeightInfoPO::getWeightGoods)
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

    private List<TCostStorageSettleDetailDTO> listDetailJG(Long cargoInfoId) {
        List<VWeightInfoPO> JGWeightInfos = jgMapper.listJGWeightInfo(cargoInfoId);
        if (JGWeightInfos.isEmpty()) {
            throw new BusinessRuntimeException("找不到过磅数据");
        }
        VWeightInfoPO earliestWeightInfo = JGWeightInfos.stream().min(Comparator.comparing(VWeightInfoPO::getWeighOutDt)).orElseThrow(null);

        List<TBusHandoverlistPO> ZCHandoverlists = jgMapper.listZCHandoverlist(cargoInfoId);
        List<TDisShipvoyageItemPO> shipvoyageItems = jgMapper.listShipvoyageItem(cargoInfoId);
        List<VWeightInfoPO> SGWeightInfos = jgMapper.listSGWeightInfo(cargoInfoId);

        LocalDate beginDate = date2LocalDate(earliestWeightInfo.getWeighOutDt());
        LocalDate endDate = LocalDate.now();
        List<LocalDate> dates = new ArrayList<>();
        LocalDate tempDate = beginDate.minusDays(1);
        while (!(tempDate = tempDate.plusDays(1)).isAfter(endDate)) {
            dates.add(tempDate);
        }

        Pair<Map<LocalDate, BigDecimal>, Map<LocalDate, BigDecimal>> transferInOutTons = getTransferInOutTons(cargoInfoId);
        Map<LocalDate, BigDecimal> mixOutTons = getMixOutTons(cargoInfoId);

        Map<LocalDate, BigDecimal> inTons = dates.stream().map(v1 -> {
            BigDecimal everydayInTon = JGWeightInfos.stream()
                    .filter(v2 -> date2LocalDate(v2.getWeighOutDt()).equals(v1))
                    .map(VWeightInfoPO::getWeightGoods)
                    .reduce(ZERO, BigDecimal::add);
            everydayInTon = everydayInTon.add(Optional.ofNullable(transferInOutTons.getLeft().get(v1)).orElse(ZERO));
            return Pair.of(v1, everydayInTon);
        }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        Map<LocalDate, BigDecimal> outTons = dates.stream().map(v1 -> {
            BigDecimal everydayOutTon = ZCHandoverlists.stream()
                    .filter(v2 -> date2LocalDate(
                            shipvoyageItems.stream()
                                    .filter(v3 -> v2.getShipvoyageItemId().equals(v3.getId()))
                                    .findFirst()
                                    .orElseThrow(null)
                                    .getWorkEndTime()
                    ).equals(v1))
                    .map(TBusHandoverlistPO::getTon)
                    .reduce(ZERO, BigDecimal::add);
            everydayOutTon = everydayOutTon.add(
                    SGWeightInfos.stream()
                            .filter(v2 -> date2LocalDate(v2.getWeighOutDt()).equals(v1))
                            .map(VWeightInfoPO::getWeightGoods)
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

    private Pair<Boolean, Integer> getTransferFreeStorageDays(Long cargoInfoId) {
        TBusCargoInfoPO cargoInfo = mapper.getCargoInfo(cargoInfoId);
        return Pair.of(cargoInfo.getRootId() != null, cargoInfo.getResidueStorage());
    }

    @Override
    public List<TCostStorageSettleDetailDTO> listDetailWithContract(Long cargoInfoId, Long contractRateId, String isUseReduce) {
        TBusContractRatePO contractRate = mapper.getContractRate(contractRateId);

        List<TCostStorageSettleDetailDTO> details = listDetailContainsHistory(cargoInfoId);

        Map<LocalDate, BigDecimal> inTons = details.stream()
                .map(v1 -> Pair.of(date2LocalDate(v1.getDate()), v1.getInTon()))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (prev, next) -> next, LinkedHashMap::new));

        Pair<Boolean, Integer> reduceDays = getReduceDays(cargoInfoId);
        Pair<Boolean, Integer> transferFreeStorageDays = getTransferFreeStorageDays(cargoInfoId);

        details.forEach(v1 -> {
            BigDecimal inTon = details.stream()
                    .filter(v2 -> !date2LocalDate(v1.getDate()).minusDays(CommonEnum.YesNoMode.YES.getCode().equals(isUseReduce) && reduceDays.getLeft() ? reduceDays.getRight() : transferFreeStorageDays.getLeft() ? transferFreeStorageDays.getRight() : Optional.ofNullable(contractRate.getFreeStorageDays()).orElse(0)).isBefore(date2LocalDate(v2.getDate())))
                    .map(TCostStorageSettleDetailPO::getInTon)
                    .reduce(ZERO, BigDecimal::add);
            BigDecimal outTon = details.stream()
                    .filter(v2 -> !date2LocalDate(v1.getDate()).isBefore(date2LocalDate(v2.getDate())))
                    .map(TCostStorageSettleDetailPO::getOutTon)
                    .reduce(ZERO, BigDecimal::add);
            BigDecimal billableTon = inTon.subtract(outTon);
            if (billableTon.compareTo(ZERO) < 0) {
                List<Map.Entry<LocalDate, BigDecimal>> tempInTons = inTons.entrySet().stream()
                        .filter(v2 -> date2LocalDate(v1.getDate()).minusDays(CommonEnum.YesNoMode.YES.getCode().equals(isUseReduce) && reduceDays.getLeft() ? reduceDays.getRight() : transferFreeStorageDays.getLeft() ? transferFreeStorageDays.getRight() : Optional.ofNullable(contractRate.getFreeStorageDays()).orElse(0)).isBefore(v2.getKey())
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
        return details;
    }

    @Override
    public List<TCostStorageSettleDTO> listStorageSettle(Long cargoInfoId) {
        return mapper.listStorageSettle(cargoInfoId);
    }

    @Override
    public void reduce(TBusStackFeeReducePO stackFeeReduce) {
        TBusStackFeeReducePO old = mapper.getStackFeeReduce(stackFeeReduce.getCargoInfoId());
        if (old == null) {
            if (StringUtils.isNotBlank(stackFeeReduce.getReduceType())) {
                stackFeeReduce.setId(snowflake.nextId());
                mapper.insertStackFeeReduce(stackFeeReduce);
            }
        } else {
            if (StringUtils.isNotBlank(stackFeeReduce.getReduceType())) {
                mapper.updateStackFeeReduce(stackFeeReduce);
            } else {
                mapper.deleteStackFeeReduce(stackFeeReduce.getCargoInfoId());
            }
        }
    }
}
