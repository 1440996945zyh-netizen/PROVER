package com.yy.ppm.statement.service.impl.storageFee;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IORuntimeException;
import org.apache.ibatis.cursor.Cursor;
import cn.hutool.core.lang.Snowflake;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yy.common.enums.CommonEnum;
import com.yy.common.excel.export.enums.ExcelTemplate;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusCustomerDTO;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import com.yy.ppm.business.bean.po.TBusCargoMixRecordPO;
import com.yy.ppm.business.bean.po.TBusContractRatePO;
import com.yy.ppm.common.enums.*;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.statement.bean.dto.ConfirmForMiscAndStorageDTO;
import com.yy.ppm.statement.bean.dto.CostBillDtoSheetTemplate;
import com.yy.ppm.statement.bean.dto.TMiscBillingExportDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import com.yy.ppm.statement.bean.dto.storageFee.*;
import com.yy.ppm.statement.bean.po.*;
import com.yy.ppm.statement.mapper.storageFee.StorageFeeMapper;
import com.yy.ppm.statement.service.storageFee.StorageFeeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static com.yy.common.util.DateUtil.*;
import static java.math.BigDecimal.ZERO;

@Service
public class StorageFeeServiceImpl implements StorageFeeService {

    @Autowired
    private StorageFeeMapper storageFeeMapper;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private CommonService commonService;

    @Autowired
    private SysFileService sysFileService;

    @Resource
    private SecurityUtils securityUtils;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private static String REDUCE_TYPE_3 = "3"; //堆存费减免类型是三  计算截止时间
    private static final int CURSOR_LIMIT = 300;
    /**
     * 票货列表
     *
     * @param parameter
     * @param query
     * @return
     */
    @Override
    public Pages<TBusCargoInfoDTO> listCargoInfo(PageParameter parameter, TBusCargoInfoQueryDTO query) {
        Pages<TBusCargoInfoDTO> result = PageHelperUtils.limit(parameter, () -> storageFeeMapper.listCargoInfo(query));
        List<TBusCargoInfoDTO> list = result.getPages();
        List<String> cargoInfoNoList = list.stream().map(e->e.getCargoInfoNo()).collect(Collectors.toList());
        List<Map<String,String>> shipVoyages = Lists.newArrayList();
        Map<String,List<Map<String,String>>> mapList = Maps.newHashMap();
        if(CollectionUtil.isNotEmpty(cargoInfoNoList)){
            shipVoyages = storageFeeMapper.getShipVoyages(cargoInfoNoList);
            mapList = shipVoyages.stream().collect(Collectors.groupingBy(e->e.get("cargoInfoNo")));
        }


        for (TBusCargoInfoDTO page : list) {
            if(CollectionUtil.isNotEmpty(mapList) && CollectionUtil.isNotEmpty(mapList.get(page.getCargoInfoNo()))){
                String shipVoyage = mapList.get(page.getCargoInfoNo()).get(0).get("shipVoyage");
                if(mapList.get(page.getCargoInfoNo()).size()>1){
                    for(Map<String,String> map : mapList.get(page.getCargoInfoNo())){
                        shipVoyage = ","+map.get("shipVoyage");
                    }
                }
                page.setShipNameVoyage(shipVoyage);
            }
            List<String> strins = storageFeeMapper.getShipAllBerthName(page.getShipvoyageItemId());
            page.setBerthName(CollectionUtils.isEmpty(strins) ? "" : String.join(",", strins));
        }
        return result;
    }


    @Override
    public Pages<TBusCargoInfoDTO> ListStatementStackFee(PageParameter parameter, TBusCargoInfoQueryDTO query) {
        Pages<TBusCargoInfoDTO> result =
                PageHelperUtils.limit(parameter, () -> storageFeeMapper.listStatementForStackFee(query));
        //回显泊位
        for (TBusCargoInfoDTO page : result.getPages()) {
            List<String> strins = storageFeeMapper.getShipAllBerthName(page.getShipvoyageItemId());
            page.setBerthName(CollectionUtils.isEmpty(strins) ? "" : String.join(",", strins));

        }
        return result;
    }

    /**
     * 查询票货来源
     *
     * @param cargoInfoId
     * @return
     */
    private CargoInfoSourceEnum getSource(Long cargoInfoId) {
        String source = storageFeeMapper.getCargoInfoSource(cargoInfoId);
        return CargoInfoSourceEnum.match(source);
    }

    /**
     * 计算日期
     *
     * @param cargoInfoId
     * @param isFinal
     * @param endDate
     * @return
     */
    private List<LocalDate> calculateDates(Long cargoInfoId, String isFinal, LocalDate endDate,String isUseReduce,String reduceType) {
        LocalDate beginDate;
        switch (getSource(cargoInfoId)) {
            case 卸船: {
                TDisShipvoyageItemPO shipvoyageItem = storageFeeMapper.getShipvoyageItem(cargoInfoId);
                if(shipvoyageItem==null){
                    throw new BusinessRuntimeException("卸船票货找不到船");
                }
                if (shipvoyageItem.getWorkStartTime() == null) {
                    throw new BusinessRuntimeException("无法计算起算日期：船舶航次尚未开工");
                }
                beginDate = date2LocalDate(shipvoyageItem.getWorkStartTime());
            }
            break;
            case 集港: {
                List<VWeightInfoPO> weightInfos = storageFeeMapper.listJGWeightInfo(cargoInfoId);
                if (weightInfos.isEmpty()) {
                    throw new BusinessRuntimeException("无法计算起算日期：找不到集港记录");
                }
                beginDate = date2LocalDate(weightInfos.stream().min(Comparator.comparing(VWeightInfoPO::getWeighOutDt)).get().getWeighOutDt());
            }
            break;
            case 货转: {
                TBusCargoInfoPO cargoInfo = storageFeeMapper.getCargoInfo(cargoInfoId);
                // todo
                if (cargoInfo.getTransferDate() == null) {
                    throw new BusinessRuntimeException("数据异常：找不到货转日期");
                }
                beginDate = date2LocalDate(cargoInfo.getTransferDate());
            }
            break;
            case 混配: {
                TBusCargoMixRecordPO cargoMixRecord = storageFeeMapper.getCargoMixRecord(cargoInfoId);
                if(cargoMixRecord.getMixTime() == null){
                    throw new BusinessRuntimeException("混配票货找不到混配日期");
                }
                beginDate = date2LocalDate(cargoMixRecord.getMixTime());
            }
            break;
            default:
                throw new BusinessRuntimeException("不支持的票货来源");
        }
        LocalDate tempEndDate = null;
        if(CommonEnum.YesNoMode.YES.getCode().equals(isUseReduce)&&REDUCE_TYPE_3.equals(reduceType)){
            TBusCargoInfoPO cargoInfo = storageFeeMapper.getCargoInfoReduce(cargoInfoId);
            if(cargoInfo==null||cargoInfo.getCalEndDate()==null){
                throw new BusinessRuntimeException("票货优惠是指定计费截止日期类型，但是未获取到指定的计费截止日期");
            }
            tempEndDate=cargoInfo.getCalEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        }else{

        TBusCargoInfoPO cargoInfo = storageFeeMapper.getCargoInfo(cargoInfoId);
        boolean isClearBool = CommonEnum.YesNoMode.YES.getCode().equals(cargoInfo.getIsClear());
        boolean isFinalBool = CommonEnum.YesNoMode.YES.getCode().equals(isFinal);
        boolean hasEndDateBool = endDate != null;

        if (!hasEndDateBool) {
            if (!isClearBool) {
                if (isFinalBool) {
                    throw new BusinessRuntimeException("无法计算止算日期：未完货时不能最终结算");
                }
                tempEndDate = LocalDate.now();
            } else {
                // todo
                if (cargoInfo.getRealClearDate() == null) {
                    throw new BusinessRuntimeException("该票货已完货，但找不到完货日期，请重新完货操作后重试");
                }
                tempEndDate = date2LocalDate(cargoInfo.getRealClearDate());
            }
        } else {
            if (isFinalBool) {
                throw new BusinessRuntimeException("无法计算止算日期：最终结算时不能指定止算日期");
            }
            if (!isClearBool) {
                if (endDate.isAfter(LocalDate.now())) {
                    throw new BusinessRuntimeException("无法计算止算日期：未完货票货指定的止算日期不能超过当前日期");
                }
            } else {
                // todo
                if (cargoInfo.getRealClearDate() == null) {
                    throw new BusinessRuntimeException("该票货已完货，但找不到完货日期，请重新完货操作后重试");
                }
                if (endDate.isAfter(date2LocalDate(cargoInfo.getRealClearDate()))) {
                    throw new BusinessRuntimeException("无法计算止算日期：已完货票货指定的止算日期不能超过完货日期");
                }
            }
            tempEndDate = endDate;
        }

        }
        return LongStream.rangeClosed(beginDate.toEpochDay(), Optional.ofNullable(tempEndDate).orElseThrow(()->new BusinessRuntimeException("获取截至计费时间失败")).toEpochDay())
                .mapToObj(LocalDate::ofEpochDay)
                .collect(Collectors.toList());
    }

    /**
     * 查询每日出场量
     *
     * @param cargoInfoId
     * @param dates
     * @return
     */
    private Map<LocalDate, BigDecimal> listOutTon(Long cargoInfoId, List<LocalDate> dates) {
        List<TBusHandoverlistPO> zcHandoverlists = storageFeeMapper.listZCHandoverlist(cargoInfoId);
        List<TDisShipvoyageItemPO> zcShipvoyageItems = storageFeeMapper.listZCShipvoyageItem(cargoInfoId);
        if(zcShipvoyageItems.isEmpty()){
            zcShipvoyageItems = storageFeeMapper.listZCShipvoyageItemByCargoInfo(cargoInfoId);
        }
        List<TDisShipvoyageItemPO> zcShipvoyageItemsFinal = zcShipvoyageItems;


        List<VWeightInfoPO> sglxWeightInfos = storageFeeMapper.listSGLXWeightInfo(cargoInfoId);

        Map<LocalDate, BigDecimal> transferOutTonMap = new HashMap<>();
        List<TBusCargoInfoPO> cargoInfos = storageFeeMapper.listTransferOutCargoInfo(cargoInfoId);
        cargoInfos.stream().collect(Collectors.groupingBy(TBusCargoInfoPO::getTransferDate)).forEach((k, v) -> {
            transferOutTonMap.put(date2LocalDate(k), v.stream().map(v1 -> Optional.ofNullable(v1.getTon()).orElse(ZERO)).reduce(ZERO, BigDecimal::add));
        });

        List<Map<String, Object>> mixOutWeights = storageFeeMapper.listMixOutWeight(cargoInfoId);
        Map<LocalDate, BigDecimal> mixOutWeightMap = mixOutWeights.stream()
                .map(v1 -> Pair.of(date2LocalDate((Date) v1.get("mixDate")), (BigDecimal) v1.get("mixWeight")))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        return dates.stream()
                .map(v1 -> {
                    BigDecimal everydayTon = ZERO;
                    everydayTon = everydayTon.add(
                            zcHandoverlists.stream()
                                    .filter(v2 -> date2LocalDate(
                                            Optional.ofNullable(
                                                    zcShipvoyageItemsFinal.stream()
                                                            .filter(v3 -> v2.getShipvoyageItemId().equals(v3.getId()))
                                                            .findFirst()
                                                            .orElseThrow(() -> new BusinessRuntimeException("找不到票货关联航次"))
                                                            .getWorkEndTime()
                                            ).orElseThrow(() -> new BusinessRuntimeException("票货关联航次没有完工时间"))
                                            ).equals(v1)
                                    )
                                    .map(TBusHandoverlistPO::getTon)
                                    .reduce(ZERO, BigDecimal::add)
                    );
                    everydayTon = everydayTon.add(
                            sglxWeightInfos.stream()
                                    .filter(v2 -> date2LocalDate(v2.getWeighOutDt()).equals(v1))
                                    .map(VWeightInfoPO::getWeightGoods)
                                    .reduce(ZERO, BigDecimal::add)
                    );
                    everydayTon = everydayTon.add(Optional.ofNullable(transferOutTonMap.get(v1)).orElse(ZERO));
                    everydayTon = everydayTon.add(Optional.ofNullable(mixOutWeightMap.get(v1)).orElse(ZERO));
                    return Pair.of(v1, everydayTon);
                })
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    /**
     * 查询每日进场量
     *
     * @param cargoInfoId
     * @param isCalculate
     * @param isFinal
     * @param dates
     * @return
     */
    private Map<LocalDate, BigDecimal> listInTon(Long cargoInfoId, String isCalculate, String isFinal, List<LocalDate> dates) {
        BigDecimal firstDayTon = ZERO;
        List<VWeightInfoPO> weightInfos = Collections.emptyList();
        switch (getSource(cargoInfoId)) {
            case 卸船: {
                if (CommonEnum.YesNoMode.YES.getCode().equals(isCalculate) || !CommonEnum.YesNoMode.YES.getCode().equals(isFinal)) {
                    List<TBusHandoverlistPO> handoverlists = storageFeeMapper.listXCHandoverlist(cargoInfoId);
                    firstDayTon = handoverlists.stream().map(TBusHandoverlistPO::getTon).reduce(ZERO, BigDecimal::add);
                } else {
                    Map<LocalDate, BigDecimal> outTons = listOutTon(cargoInfoId, dates);
                    firstDayTon = outTons.values().stream().reduce(ZERO, BigDecimal::add);
                }
            }
            break;
            case 集港: {
                weightInfos = storageFeeMapper.listJGWeightInfo(cargoInfoId);
            }
            break;
            case 货转: {
                TBusCargoInfoPO cargoInfo = storageFeeMapper.getCargoInfo(cargoInfoId);
                firstDayTon = cargoInfo.getTon();
            }
            break;
            case 混配: {
                TBusCargoMixRecordPO cargoMixRecord = storageFeeMapper.getCargoMixRecord(cargoInfoId);
                firstDayTon = cargoMixRecord.getMixWeight();
            }
            break;
        }

        BigDecimal finalFirstDayTon = firstDayTon;
        List<VWeightInfoPO> finalWeightInfos = weightInfos;
        return dates.stream()
                .map(v1 -> {
                    BigDecimal everydayTon = ZERO;
                    if (v1.equals(dates.get(0))) {
                        everydayTon = everydayTon.add(finalFirstDayTon);
                    }
                    everydayTon = everydayTon.add(
                            finalWeightInfos.stream()
                                    .filter(v2 -> date2LocalDate(v2.getWeighOutDt()).equals(v1))
                                    .map(VWeightInfoPO::getWeightGoods)
                                    .reduce(ZERO, BigDecimal::add)
                    );
                    return Pair.of(v1, everydayTon);
                })
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    /**
     * 结算明细列表
     *
     * @param cargoInfoId
     * @param isCalculate
     * @param isFinal
     * @param endDate
     * @return
     */
    public List<TCostStorageSettleDetailDTO> listDetailContainsHistory(Long cargoInfoId, String isCalculate, String isFinal, LocalDate endDate,String isUseReduce,String reduceType) {
        List<LocalDate> dates = calculateDates(cargoInfoId, isFinal, endDate, isUseReduce,reduceType);

        Map<LocalDate, BigDecimal> inTons = listInTon(cargoInfoId, isCalculate, isFinal, dates);
        Map<LocalDate, BigDecimal> outTons = listOutTon(cargoInfoId, dates);

        Map<LocalDate, BigDecimal> tons = new HashMap<>();
        for (int i = 0; i < dates.size(); i++) {
            LocalDate today = dates.get(i);
            LocalDate tomorrow = dates.stream().filter(v1 -> v1.plusDays(1).equals(today)).findFirst().orElse(null);
            BigDecimal yesterdayTon;
            if (tomorrow == null) {
                yesterdayTon = ZERO;
            } else {
                yesterdayTon = tons.get(tomorrow);
            }
            BigDecimal ton = yesterdayTon.add(inTons.get(today)).subtract(outTons.get(today));
            tons.put(today, ton);
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
                    detail.setCargoInfoId(cargoInfoId);
                    return detail;
                })
                .collect(Collectors.toList());
    }

    /**
     * 过滤已结算明细
     *
     * @param cargoInfoId
     * @param details
     * @return
     */
    private List<TCostStorageSettleDetailDTO> filterHistoryDetail(Long cargoInfoId, List<TCostStorageSettleDetailDTO> details) {
        List<TCostStorageSettleDetailDTO> tempDetails = storageFeeMapper.listStorageSettleDetail(cargoInfoId);
        return details.stream()
                .filter(v1 -> tempDetails.stream().noneMatch(v2 -> v1.getDate().equals(v2.getDate())))
                .collect(Collectors.toList());
    }

    /**
     * 待结算明细列表
     *
     * @param cargoInfoId
     * @param isFinal
     * @param endDate
     * @param isXC
     * @return
     */
    @Override
    public List<TCostStorageSettleDetailDTO> listDetail(Long cargoInfoId, String isCalculate, String isFinal, LocalDate endDate, String isXC,String isUseReduce,String reduceType) {
        List<TCostStorageSettleDetailDTO> details = listDetailContainsHistory(cargoInfoId, isCalculate, isFinal, endDate,isUseReduce,reduceType);
        if (CommonEnum.YesNoMode.YES.getCode().equals(isFinal) && CommonEnum.YesNoMode.YES.getCode().equals(isXC)) {
            return details;
        } else {
            return filterHistoryDetail(cargoInfoId, details);
        }
    }

    /**
     * 合同列表
     *
     * @param cargoInfoId
     * @param date
     * @return
     */
    @Override
    public List<Map<String, Object>> listContract(Long cargoInfoId, Date date) {
        return storageFeeMapper.listContract(cargoInfoId, date);
    }

    /**
     * 获取减免日期
     *
     * @param cargoInfoId
     * @param beginDate
     * @return
     */
    private int getReduceDays(Long cargoInfoId, LocalDate beginDate) {
        TBusStackFeeReducePO stackFeeReduce = storageFeeMapper.getStackFeeReduce(cargoInfoId);
        if(stackFeeReduce!=null){
            //todo 堆存费优惠内容清空时判断  好像也不需要
            if(stackFeeReduce.getReduceType()==null){

            }
        }else {
            throw new BusinessRuntimeException("明细列表计算失败：该票货无减免信息，无法使用减免");

        }

/*        boolean hasReduce = stackFeeReduce != null && StringUtils.isNotBlank(stackFeeReduce.getReduceType());
        if (!hasReduce) {
            throw new BusinessRuntimeException("明细列表计算失败：该票货无减免信息，无法使用减免");
        }*/

        int reduceDays = 0;
        if (ReduceTypeEnum._0.getCode().equals(stackFeeReduce.getReduceType())) {
            reduceDays = Character.MAX_VALUE;
        }
        if (ReduceTypeEnum._1.getCode().equals(stackFeeReduce.getReduceType())) {
            reduceDays = stackFeeReduce.getReduceDays();
        }
        if (ReduceTypeEnum._2.getCode().equals(stackFeeReduce.getReduceType())) {
            reduceDays = (int) Math.max(getDateDifference(beginDate, date2LocalDate(stackFeeReduce.getReduceEndDate())), 0);
        }
        return reduceDays;
    }

    /**
     * 计算结算金额
     *
     * @param cargoInfoId
     * @param freeStorageDays
     * @param rate
     * @param tax
     * @param isUseReduce
     * @return
     */
    public void calculateAmount(List<TCostStorageSettleDetailDTO> details, Long cargoInfoId, Integer freeStorageDays, BigDecimal rate, BigDecimal tax,
                                String isUseReduce,String reduceType) {
        Map<LocalDate, BigDecimal> inTons = details.stream()
                .map(v1 -> Pair.of(date2LocalDate(v1.getDate()), v1.getInTon()))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (prev, next) -> next, LinkedHashMap::new));

        Integer reduceDays;
        if (CommonEnum.YesNoMode.YES.getCode().equals(isUseReduce)&& !REDUCE_TYPE_3.equals(reduceType)) {
            reduceDays = getReduceDays(cargoInfoId, date2LocalDate(details.get(0).getDate()));
        } else {
            reduceDays = null;
        }

        details.forEach(v1 -> {
            {
                BigDecimal inTon = details.stream()
                        .filter(v2 -> !date2LocalDate(v1.getDate()).minusDays(freeStorageDays).isBefore(date2LocalDate(v2.getDate())))
                        .map(TCostStorageSettleDetailDTO::getInTon)
                        .reduce(ZERO, BigDecimal::add);
                BigDecimal outTon = details.stream()
                        .filter(v2 -> !date2LocalDate(v1.getDate()).isBefore(date2LocalDate(v2.getDate())))
                        .map(TCostStorageSettleDetailDTO::getOutTon)
                        .reduce(ZERO, BigDecimal::add);
                BigDecimal billableTon = inTon.subtract(outTon);
                if (billableTon.compareTo(ZERO) < 0) {
                    List<Map.Entry<LocalDate, BigDecimal>> tempInTons = inTons.entrySet().stream()
                            .filter(v2 -> date2LocalDate(v1.getDate()).minusDays(freeStorageDays).isBefore(v2.getKey())
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
                v1.setOriginalBillableTon(ZERO.max(billableTon));
                v1.setBillableTon(v1.getOriginalBillableTon());
            }
            if (reduceDays != null) {
                BigDecimal inTon = details.stream()
                        .filter(v2 -> !date2LocalDate(v1.getDate()).minusDays(reduceDays).isBefore(date2LocalDate(v2.getDate())))
                        .map(TCostStorageSettleDetailDTO::getInTon)
                        .reduce(ZERO, BigDecimal::add);
                BigDecimal outTon = details.stream()
                        .filter(v2 -> !date2LocalDate(v1.getDate()).isBefore(date2LocalDate(v2.getDate())))
                        .map(TCostStorageSettleDetailDTO::getOutTon)
                        .reduce(ZERO, BigDecimal::add);
                BigDecimal billableTon = inTon.subtract(outTon);
                if (billableTon.compareTo(ZERO) < 0) {
                    List<Map.Entry<LocalDate, BigDecimal>> tempInTons = inTons.entrySet().stream()
                            .filter(v2 -> date2LocalDate(v1.getDate()).minusDays(reduceDays).isBefore(v2.getKey())
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
            }
            v1.setOriginalAmount(v1.getOriginalBillableTon().multiply(rate).setScale(2, RoundingMode.HALF_UP));
            v1.setAmount(v1.getBillableTon().multiply(rate).setScale(2, RoundingMode.HALF_UP));
            v1.setTaxAmount(
                    v1.getAmount().divide(
                            BigDecimal.ONE.add(
                                    tax.divide(
                                            BigDecimal.valueOf(100),
                                            4, RoundingMode.HALF_UP
                                    )
                            ), 100, RoundingMode.HALF_UP
                    ).multiply(
                            tax.divide(
                                    BigDecimal.valueOf(100),
                                    4, RoundingMode.HALF_UP
                            )
                    ).setScale(2, RoundingMode.HALF_UP)
            );
        });
    }

    /**
     * 待结算明细列表（已选合同）
     *
     * @param cargoInfoId
     * @param isCalculate
     * @param isFinal
     * @param endDate
     * @param freeStorageDays
     * @param rate
     * @param tax
     * @param isUseReduce
     * @param isXC
     * @return
     */
    @Override
    public List<TCostStorageSettleDetailDTO> listDetailWithContract(Long cargoInfoId, String isCalculate, String isFinal, LocalDate endDate, Integer freeStorageDays, BigDecimal rate, BigDecimal tax, String isUseReduce, String isXC,String reduceType) {
        List<TCostStorageSettleDetailDTO> details = listDetailContainsHistory(cargoInfoId, isCalculate, isFinal, endDate,isUseReduce,reduceType);
        calculateAmount(details, cargoInfoId, freeStorageDays, rate, tax, isUseReduce,reduceType);
        if (CommonEnum.YesNoMode.YES.getCode().equals(isFinal) && CommonEnum.YesNoMode.YES.getCode().equals(isXC)) {
            return details;
        } else {
            return filterHistoryDetail(cargoInfoId, details);
        }
    }

    /**
     * 结算
     *
     * @param storageSettle
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void settle(TCostStorageSettleDTO storageSettle) {
        List<TCostStorageSettleDTO> storageSettles = storageFeeMapper.listStorageSettle(storageSettle.getCargoInfoId());
        boolean hasFinalBool = storageSettles.stream().anyMatch(v1 -> CommonEnum.YesNoMode.YES.getCode().equals(v1.getIsFinal()));
        if (hasFinalBool) {
            throw new BusinessRuntimeException("结算失败：该票货已最终结算，无法再次结算");
        }

        boolean isXCFinalBool = CargoInfoSourceEnum.卸船 == getSource(storageSettle.getCargoInfoId()) && CommonEnum.YesNoMode.YES.getCode().equals(storageSettle.getIsFinal());
        if (!isXCFinalBool) {
            List<TCostStorageSettleDetailDTO> details = storageFeeMapper.listStorageSettleDetail(storageSettle.getCargoInfoId());
            List<LocalDate> overlap = getOverlap(
                    details.stream().map(v1 -> date2LocalDate(v1.getDate())).collect(Collectors.toList()),
                    storageSettle.getDetails().stream().map(v1 -> date2LocalDate(v1.getDate())).collect(Collectors.toList())
            );
            if (!overlap.isEmpty()) {
                throw new BusinessRuntimeException(String.format("结算失败：重复的结算日期【%s至%s】", overlap.get(0), overlap.get(overlap.size() - 1)));
            }
        }

        storageSettle.setId(snowflake.nextId());
        if (!isXCFinalBool) {
            storageSettle.setAmount(storageSettle.getDetails().stream().map(TCostStorageSettleDetailDTO::getAmount).reduce(ZERO, BigDecimal::add));
        } else {
            storageSettle.setAmount(storageSettle.getDetails().stream().map(TCostStorageSettleDetailDTO::getAmount).reduce(ZERO, BigDecimal::add).subtract(storageSettles.stream().map(TCostStorageSettlePO::getAmount).reduce(ZERO, BigDecimal::add)));
        }
        storageSettle.setReduceAmount(storageSettle.getDetails().stream().map(TCostStorageSettleDetailDTO::getOriginalAmount).reduce(ZERO, BigDecimal::add).subtract(storageSettle.getAmount()));
        storageSettle.setTaxAmount(storageSettle.getDetails().stream().map(TCostStorageSettleDetailDTO::getTaxAmount).reduce(ZERO, BigDecimal::add));
        storageSettle.setStartDate(storageSettle.getDetails().stream().min(Comparator.comparing(TCostStorageSettleDetailDTO::getDate)).orElseThrow(null).getDate());
        storageSettle.setEndDate(storageSettle.getDetails().stream().max(Comparator.comparing(TCostStorageSettleDetailDTO::getDate)).orElseThrow(null).getDate());
        storageSettle.setStatus(StorageSettleStatusEnum._10.getCode());
        storageFeeMapper.insertStorageSettle(storageSettle);

        storageSettle.getDetails().forEach(v1 -> {
            v1.setId(snowflake.nextId());
            v1.setStorageSettleId(storageSettle.getId());
        });
        storageFeeMapper.insertStorageSettleDetail(storageSettle.getDetails());

        //回写票货的结算状态
        String settleStatusCode = storageFeeMapper.getSettleStatusByCargoInfoId(storageSettle.getCargoInfoId());
        String settleStatusCodeName= null;
        if("10".equals(settleStatusCode)){
            settleStatusCodeName = "未结算";
        }else if ("20".equals(settleStatusCode)){
            settleStatusCodeName = "预结算";
        }else if ("30".equals(settleStatusCode)){
            settleStatusCodeName = "最终结算";
        }

        storageFeeMapper.updateCargoInfoStatementStatus(storageSettle.getCargoInfoId(),settleStatusCode,settleStatusCodeName);
    }

    /**
     * 堆存费结算列表
     *
     * @param cargoInfoId
     * @return
     */
    @Override
    public List<TCostStorageSettleDTO> listStorageSettle(Long cargoInfoId) {
        return storageFeeMapper.listStorageSettle(cargoInfoId);
    }

    /**
     * 撤销结算
     *
     * @param storageSettleId
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void cancelSettle(Long storageSettleId) {
        TCostStorageSettlePO storageSettle = storageFeeMapper.getStorageSettle(storageSettleId);
        if (storageSettle == null) {
            throw new BusinessRuntimeException("撤销结算失败：不存在的结算");
        }
        if (!StorageSettleStatusEnum._10.getCode().equals(storageSettle.getStatus())) {
            throw new BusinessRuntimeException("撤销结算失败：已审核无法撤销结算，请先销审");
        }

        storageFeeMapper.deleteStorageSettle(storageSettleId);
        storageFeeMapper.deleteStorageSettleDetail(storageSettleId);

        //回写票货的结算状态
        String settleStatusCode = storageFeeMapper.getSettleStatusByCargoInfoId(storageSettle.getCargoInfoId());
        String settleStatusCodeName= null;
        if("10".equals(settleStatusCode)){
            settleStatusCodeName = "未结算";
        }else if ("20".equals(settleStatusCode)){
            settleStatusCodeName = "预结算";
        }else if ("30".equals(settleStatusCode)){
            settleStatusCodeName = "最终结算";
        }
        storageFeeMapper.updateCargoInfoStatementStatus(storageSettle.getCargoInfoId(),settleStatusCode,settleStatusCodeName);
    }

    /**
     * 审核
     *
     * @param storageSettleId
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void review(Long storageSettleId) {
        TCostStorageSettlePO storageSettle = storageFeeMapper.getStorageSettle(storageSettleId);
        if (!StorageSettleStatusEnum._10.getCode().equals(storageSettle.getStatus())) {
            throw new BusinessRuntimeException("审核失败：已审核无法再次审核");
        }

        TBusCargoInfoPO cargoInfo = storageFeeMapper.getCargoInfo(storageSettle.getCargoInfoId());

        TCostStatementPO statement = new TCostStatementPO();
        statement.setId(snowflake.nextId());
        statement.setCompanyId(storageSettle.getCompanyId());
        statement.setCompanyName(storageSettle.getCompanyName());
        statement.setStatementNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.STATEMENT_NO, null));
        statement.setCustomerId(storageSettle.getCargoOwnerId());
        statement.setCustomerName(storageSettle.getCargoOwnerName());
        statement.setType(HandoverlistTypeEnum._50.getCode());
        statement.setShipvoyageId(storageSettle.getShipvoyageId());
        statement.setShipvoyageItemId(storageSettle.getShipvoyageItemId());
        statement.setCargoCode(cargoInfo.getCargoCode());
        statement.setCargoName(cargoInfo.getCargoName());
        statement.setTradeType(cargoInfo.getTradeType());
        statement.setSettlementDate(storageSettle.getCreateTime());
        statement.setStatus(StatementStatusEnum._30.getCode());
        statement.setIsFinal(StringUtils.isEmpty(storageSettle.getIsFinal()) ? "0" : storageSettle.getIsFinal());
        storageFeeMapper.insertStatement(statement);

        TBusContractRatePO contractRate = storageFeeMapper.getContractRate(storageSettle.getContractRateId());

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
        detail.setNumber(storageSettle.getRate().compareTo(ZERO) == 0 ? ZERO : storageSettle.getAmount().divide(storageSettle.getRate(), 2, RoundingMode.HALF_UP));
        detail.setAmount(storageSettle.getAmount());
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
        detail.setInvoiceNumber(ZERO);
        detail.setInvoiceAmount(ZERO);
        detail.setBusinessId(storageSettle.getId());
        detail.setRateId(storageSettle.getContractRateId());
        storageFeeMapper.insertStatementDetail(detail);

        storageSettle = new TCostStorageSettlePO();
        storageSettle.setId(storageSettleId);
        storageSettle.setStatus(StorageSettleStatusEnum._20.getCode());
        storageFeeMapper.review(storageSettle);
    }

    /**
     * 销审
     *
     * @param storageSettleId
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void cancelReview(Long storageSettleId) {
        TCostStorageSettlePO storageSettle = storageFeeMapper.getStorageSettle(storageSettleId);
        if (StorageSettleStatusEnum._10.getCode().equals(storageSettle.getStatus())) {
            throw new BusinessRuntimeException("销审失败：未审核无需销审");
        }
        TCostStatementPO statement = storageFeeMapper.getStatement(storageSettleId);
        if (!StatementStatusEnum._30.getCode().equals(statement.getStatus())) {
            throw new BusinessRuntimeException("销审失败：结算单已开票，无法销审");
        }

        storageSettle = new TCostStorageSettlePO();
        storageSettle.setId(storageSettleId);
        storageSettle.setStatus(StorageSettleStatusEnum._10.getCode());
        storageFeeMapper.cancelReview(storageSettle);

        storageFeeMapper.deleteCostStatement(statement.getId());
        storageFeeMapper.deleteCostStatementDetail(statement.getId());
    }

    @Override
    public void saveConfirmFile(ConfirmForMiscAndStorageDTO dto) {
        if (CollectionUtils.isEmpty(dto.getFileIds())) {
            throw new BusinessRuntimeException("请先上传文件再进行保存");
        }
        dto.getIds().forEach(o -> {
            sysFileService.saveFileBusRelation(dto.getFileIds(), o);
        });
    }

    @Override
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


            TCostStatementPO costStatement = storageFeeMapper.getStatementById(tmpId);
            if (!StatementStatusEnum._30.getCode().equals(costStatement.getStatus())) {
                throw new BusinessRuntimeException("商务确认失败，状态非计费审核");
            }
            TCostStatementPO tCostStatementPO = new TCostStatementPO();
            tCostStatementPO.setConfirmBy(securityUtils.getLoginUserId());
            tCostStatementPO.setConfirmByName(securityUtils.getLoginUserName());
            tCostStatementPO.setConfirmTime(new Date());
            tCostStatementPO.setStatus(StatementStatusEnum._31.getCode());
            tCostStatementPO.setReceiptRemark(dto.getReceiptRemark());
            tCostStatementPO.setTaxInvoiceCode(dto.getTaxInvoiceCode());
            tCostStatementPO.setTaxInvoiceName(dto.getTaxInvoiceName());
            tCostStatementPO.setId(tmpId);
            storageFeeMapper.updateCostStatement(tCostStatementPO);
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

            TCostStatementPO costStatement = storageFeeMapper.getStatementById(tmpId);
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
            storageFeeMapper.updateCostStatement(costStatement);
        });
    }


    @Override
    public List<TCostStorageSettleDTO> listStorageSettleById(Long storageSettleId) {
        return storageFeeMapper.listStorageSettleById(storageSettleId);

    }

    /**
     * 堆存费 打印账单
     *
     * @param dto
     * @return
     */
    @Override
    public CostBillDtoSheetTemplate printFeeList(TReqStorageStatementExportDTO dto) {
//        List<TBusHandoverlistDTO> dataList = mapper.getHandoverListByIds(ids);
        List<Long> ids = dto.getIds().stream().distinct().collect(Collectors.toList());
        List<TCostStatementDetailDTO> dataDetailList = new ArrayList<>();
        StringBuilder statementName = new StringBuilder();
        StringBuilder reviewName = new StringBuilder();
        StringBuilder confirmName = new StringBuilder();
        StringBuilder contractNo = new StringBuilder();
        StringBuilder settlementBasisName = new StringBuilder();
        dataDetailList = storageFeeMapper.getStorageSettleStatementList(ids);

        TBusCustomerDTO customerDTO = storageFeeMapper.getCargoInfoWithCustomerInfo(ids.get(0));
        CostBillDtoSheetTemplate result = new CostBillDtoSheetTemplate();
        result.setAmount(ZERO);
        result.setNumberCount(ZERO);
        result.setTin(customerDTO.getTin());
        result.setBank(customerDTO.getBank());
        result.setBankAccount(customerDTO.getBankAccount());
        result.setContactNumber(customerDTO.getContactNumber());
        result.setAddress(customerDTO.getAddress());

        result.setCustomerName(customerDTO.getCustomerName());
        List<TCostStatementDetailDTO> tmpCountStatements = new ArrayList<>(3);
        if(!dataDetailList.stream().anyMatch(o->"1".equals(o.getIsFinal()))){
            result.setOutSideRemark("本次结算为预结算，完货后进行最终结算，多退少补。");
        }


        if (!CollectionUtils.isEmpty(dataDetailList)) {
            result.setReviewTime(DateUtils.formatDate(dataDetailList.get(0).getReviewTime(), CommonEnum.DateFormatType.E_1.getCode()));
            dataDetailList = dataDetailList.stream().filter(
                    o -> (o.getNumber() != null)
                            && BigDecimal.ZERO.compareTo(o.getNumber()) != 0)
                    .collect(Collectors.toList());

        }
        if("2".equals(dto.getRouteId())) {
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
        }else{
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

            //包干费
            List<TCostStatementDetailDTO> BGFList = storageFeeMapper.getBGFeeListStatement(ids);
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
//        List<Long> cargoInfoIdList = dataList.stream().map(TBusHandoverlistDTO::getCargoInfoId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(ids)) {
                List<TCostStatementDetailDTO> MISCList = storageFeeMapper.getMISCStatementList(ids);
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

        }


        //合同编号
        if (contractNo.length() > 1) {
            result.setContractNo(contractNo.substring(0, contractNo.length() - 1));
        }
        result.setConfirmByName(confirmName.length() > 1 ? confirmName.substring(0, confirmName.length() - 1) : "");
        result.setReviewByName(reviewName.length() > 1 ? reviewName.substring(0, reviewName.length() - 1) : "");
        result.setStatementByName(statementName.length() > 1 ? statementName.substring(0, statementName.length() - 1) : "");


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
        if(!tmpCountStatements.isEmpty()){
            tmpCountStatements.forEach(o -> {
                result.setAmount(result.getAmount().add(o.getAmount()));
                result.setNumberCount(result.getNumberCount().add(o.getNumber()));
            });
        }


        result.setCostNumberName("结算天数");
        result.setDetailList(dataDetailList);
        result.setSheetName("库场使用费账单");

        result.setConfirmByName(confirmName.length() > 1 ? confirmName.substring(0, confirmName.length() - 1) : "");
        result.setReviewByName(reviewName.length() > 1 ? reviewName.substring(0, reviewName.length() - 1) : "");
        result.setStatementByName(statementName.length() > 1 ? statementName.substring(0, statementName.length() - 1) : "");
        result.setContractNo(contractNo.length() > 1 ? contractNo.substring(0, contractNo.length() - 1) : "");
        result.setSettlementBasisName(settlementBasisName.length() > 1 ? settlementBasisName.substring(0, settlementBasisName.length() - 1) : "");
        result.setCompanyName(dataDetailList.get(0).getCompanyName());

        return result;
    }

    @Override
    public TBusCargoInfoDTO getTaxInvoiceCode(Long id) {
        if (id==null){
            throw new BusinessRuntimeException("没有货主id");
        }
        return  storageFeeMapper.getTaxInvoiceCode(id);
    }

    @Override
    public void exportDetail(TStorageCostDetailExportDTO dto, HttpServletResponse response)  {
        InputStream
                templatePathName = this.getClass().getClassLoader().getResourceAsStream("exceltemplates/堆存费结算导出模板.xlsx");

        try (ExcelWriter excelWriter = EasyExcel.write().file(response.getOutputStream()).withTemplate(templatePathName).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            excelWriter.fill(dto.getDetails(), fillConfig, writeSheet);
            Map<String, Object> map = MapUtils.newHashMap();
            map.put("cargoOwnerName",dto.getCargoOwnerName());
            map.put("cargoName",dto.getCargoName());
            map.put("shipNameVoyage",dto.getShipNameVoyage());
            map.put("freeStorageDays",dto.getFreeStorageDays());
            map.put("endWorkTime",dto.getEndWorkTime());
            map.put("berthTime",dto.getBerthTime());
            map.put("allAmount",dto.getAmount());
            excelWriter.fill(map, writeSheet);

            }catch (Exception exception){
                throw new BusinessRuntimeException(exception.getMessage());
            }
    }

    /**
     * 获取票货的交接清单量
     * @param cargoInfoId
     * @return
     */
    @Override
    public BigDecimal getHandoverlistTon(Long cargoInfoId) {
        return storageFeeMapper.getHandoverlistTon(cargoInfoId);
    }

    @Override
    public List<Map<String, Object>> getMixRecordList(Long cargoInfoId) {
        return storageFeeMapper.getMixRecordList(cargoInfoId);
    }

    @Override
    public byte[] pageExport(TBusCargoInfoQueryDTO query) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os,TBusCargoInfoExportDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(sttus->{
                try (Cursor<TBusCargoInfoExportDTO>  pageExport= storageFeeMapper.pageExport(query))  {
                    Iterator<TBusCargoInfoExportDTO> iterator = pageExport.iterator();
                    while(iterator.hasNext()){
                        List<TBusCargoInfoExportDTO> objects = new ArrayList<>();
                        for (int i = 0; i < CURSOR_LIMIT && iterator.hasNext(); i++) {
                            objects.add(iterator.next());
                        }
                        List<Map<String,String>> shipVoyages = storageFeeMapper.getShipVoyagesForExport(objects.stream().map(TBusCargoInfoExportDTO::getId).filter(Objects::nonNull).collect(Collectors.toList()));
                        Map<String,List<Map<String,String>>> mapList = shipVoyages.stream().collect(Collectors.groupingBy(e-> String.valueOf(e.get("cargoInfoId"))));
                        if(CollectionUtil.isNotEmpty(mapList)){
                            objects.forEach(o->{
                                if(CollectionUtil.isNotEmpty(mapList.get(o.getId().toString()))){
                                    StringBuilder shipVoyage = new StringBuilder(mapList.get(o.getId().toString()).get(0).get("shipVoyage"));
                                    if(mapList.get(o.getId().toString()).size()>1){
                                        shipVoyage.setLength(0);
                                        for(Map<String,String> map : mapList.get(o.getId().toString())){
                                            shipVoyage.append(",") .append(map.get("shipVoyage"));
                                        }
                                    }
                                    o.setShipNameVoyage(shipVoyage.delete(0,1).toString());
                                }
                            });
                        }
                        excelWriter.write(objects, writeSheet);
                    }
                } catch (Exception e) {
                    throw new IORuntimeException("堆存费页面导出异常"+e.getMessage());
                }
            });

        }
        return os.toByteArray();
    }
}
