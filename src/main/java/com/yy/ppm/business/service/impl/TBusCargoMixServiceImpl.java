package com.yy.ppm.business.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.enums.CommonEnum;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtil;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusCargoMix.TBusCargoMixRecordDTO;
import com.yy.ppm.business.bean.dto.TBusCargoMix.TBusCargoMixRecordQueryDTO;
import com.yy.ppm.business.bean.dto.TBusCargoMix.TPrdPortStorageDTO;
import com.yy.ppm.business.bean.dto.TBusCargoMix.TPrdPortStorageQueryDTO;
import com.yy.ppm.business.bean.dto.TBusRateDTO;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import com.yy.ppm.business.bean.po.TBusCargoMixDetailPO;
import com.yy.ppm.business.bean.po.TBusCargoMixRecordPO;
import com.yy.ppm.business.bean.po.TBusTrustCargoPO;
import com.yy.ppm.business.mapper.TBusCargoMixMapper;
import com.yy.ppm.business.service.TBusCargoMixService;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.common.enums.HandoverlistStatusEnum;
import com.yy.ppm.common.enums.InoutStorageEnum;
import com.yy.ppm.common.service.BusinessCommonService;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.common.service.PublicService;
import com.yy.ppm.dispatch.bean.dto.ShipVoyageDto;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import com.yy.ppm.statement.bean.po.TCostStorageSettlePO;
import com.yy.ppm.statement.bean.po.TMiscBillingPO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TBusCargoMixServiceImpl implements TBusCargoMixService {

    @Autowired
    private TBusCargoMixMapper tBusCargoMixMapper;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private CommonService commonService;

    @Autowired
    private BusinessCommonService businessCommonService;

    @Autowired
    private PublicService publicService;

    private static final String PROCSS_CD_HP = "1032"; // 完货状态-已完货
    private static final String PROCSS_NM_HP = "港口作业包干费(粮食混配)"; // 完货状态-已完货


    /**
     * 查询港存
     *
     * @param query
     * @return
     */
    @Override
    public List<TPrdPortStorageDTO> listPortStorage(TPrdPortStorageQueryDTO query) {
        return tBusCargoMixMapper.listPortStorage(query);
    }

    /**
     * 查询合同
     *
     * @param cargoInfoIds
     * @return
     */
    @Override
    public List<Map<String, Object>> contracts(List<Long> cargoInfoIds) {
        List<Map<String, Object>> firstInDates = tBusCargoMixMapper.listFirstInDate(cargoInfoIds);
        if (firstInDates.isEmpty()) {
            return Collections.emptyList();
        }
        return tBusCargoMixMapper.contracts(firstInDates);
    }

    /**
     * 混配新票货 （审核前 还没生成新票货）
     *
     * @param dto
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void mix(TBusCargoMixRecordDTO dto) {
        TBusCargoMixRecordPO po = new TBusCargoMixRecordPO();
        BeanUtils.copyProperties(dto, po);
        po.setId(snowflake.nextId());
        po.setMixWeight(dto.getDetails().stream().map(TBusCargoMixDetailPO::getMixWeight).reduce(BigDecimal.ZERO, BigDecimal::add));
        po.setStatus("10");
        //船名航次处理
        if(dto.getShipvoyageItemId()!=null){
            ShipVoyageDto shipVoyageDto = tBusCargoMixMapper.getShipVoyageInfo(dto.getShipvoyageItemId());
            po.setShipName(shipVoyageDto.getShipName());
            po.setVoyage(shipVoyageDto.getVoyage());
            po.setShipvoyageId(shipVoyageDto.getShipvoyageId());
            po.setShipvoyageItemId(shipVoyageDto.getId());
        }
        tBusCargoMixMapper.insertCargoMixRecord(po);

        List<Map<String, Object>> firstInDates = tBusCargoMixMapper.listFirstInDate(dto.getDetails().stream().map(TBusCargoMixDetailPO::getCargoInfoId).distinct().collect(Collectors.toList()));

        if(!firstInDates.isEmpty()){//没有入库时间的一般为老数据  老数据入库时间取开工时间、集港时间 最早的一个
            for (Map<String, Object> firstInDate : firstInDates) {
                if(firstInDate.get("firstInDate") == null || StringUtils.isEmpty(String.valueOf(firstInDate.get("firstInDate")))){
                    //根据航次子表id  查 航次开工时间
                    String shipWorkTime = tBusCargoMixMapper.getShipWorkStartTime(String.valueOf(firstInDate.get("cargoInfoId")));
                    //集港时间
                    String earlyJGTime = tBusCargoMixMapper.getJGTimeByCargoInfoId(String.valueOf(firstInDate.get("cargoInfoId"))) ;
                    //比较 取最早  都取不出来 就用2023-10-12

                    try {
                        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

                    if((StringUtils.isNotEmpty(earlyJGTime))&&(StringUtils.isNotEmpty(shipWorkTime))){//比较大小
                        LocalDateTime workShipLocalDateTime = DateUtils.parseDate(shipWorkTime, "yyyy-MM-dd HH:mm:ss").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        LocalDateTime JGLocalDateTime = DateUtils.parseDate(earlyJGTime, "yyyy-MM-dd HH:mm:ss").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        if(workShipLocalDateTime.compareTo(JGLocalDateTime)>0){
                            firstInDate.put("firstInDate",yyyyMMdd.parse(shipWorkTime));
                        }else {
                            firstInDate.put("firstInDate",yyyyMMdd.parse(earlyJGTime));
                        }
                    }else if(StringUtils.isEmpty(earlyJGTime)&&(StringUtils.isNotEmpty(shipWorkTime))){//空集港时间
                        firstInDate.put("firstInDate",yyyyMMdd.parse(shipWorkTime));
                    }else if((StringUtils.isNotEmpty(earlyJGTime))&&(StringUtils.isEmpty(shipWorkTime))) {//空船舶开工时间
                        firstInDate.put("firstInDate",yyyyMMdd.parse(earlyJGTime));
                    }else if(StringUtils.isEmpty(earlyJGTime)&&StringUtils.isEmpty(shipWorkTime)){
                        firstInDate.put("firstInDate",yyyyMMdd.parse("2023-10-12"));
                    }
                    }catch (Exception e){
                        throw new BusinessRuntimeException("日期转换异常");
                    }
                }
            }
        }
        //获取港存信息
        dto.getDetails().forEach(v1 -> {


            Map<String,Object> storageCargoStackTonMap= tBusCargoMixMapper.getStorageCargoStackTon(v1);
            if(CollectionUtils.isEmpty(storageCargoStackTonMap)){
                throw new BusinessRuntimeException("没有港存信息");
            }

            if(new BigDecimal(String.valueOf(storageCargoStackTonMap.get("ton"))).compareTo(v1.getMixWeight())<0){
                throw new BusinessRuntimeException("票货号："+String.valueOf(storageCargoStackTonMap.get("cargoInfoNo"))+
                        "</br>区域/垛位："+String.valueOf(storageCargoStackTonMap.get("regionName"))
                        +"/"+String.valueOf(storageCargoStackTonMap.get("massName"))+"港存不足");
            }
            v1.setId(snowflake.nextId());
            v1.setMixRecordId(po.getId());
            Object o = firstInDates.stream().filter(v2 -> v1.getCargoInfoId().equals(Long.valueOf(String.valueOf(v2.get("cargoInfoId"))))).findFirst().orElse(Collections.emptyMap()).get("firstInDate");
            LocalDate firstDate =
                    DateUtil.date2LocalDate((Date) o);
            long dateDifference = DateUtil.getDateDifference(DateUtil.date2LocalDate(dto.getMixTime()), firstDate.plusDays(v1.getFreeStorageDays()));
            v1.setRemainFreeStorageDays((int) Math.max(dateDifference, 0));
        });
        tBusCargoMixMapper.insertCargoMixDetail(dto.getDetails());
    }

    /**
     * 查询混配记录
     *
     * @param parameter
     * @param query
     * @return
     */
    @Override
    public Pages<TBusCargoMixRecordDTO> listMix(PageParameter parameter, TBusCargoMixRecordQueryDTO query) {
        return PageHelperUtils.limit(parameter, () -> tBusCargoMixMapper.listMix(query));
    }

    /**
     * 回显
     *
     * @param id
     * @return
     */
    @Override
    public TBusCargoMixRecordDTO getMix(Long id) {
        return tBusCargoMixMapper.getMix(id);
    }

    /**
     * 删除混配
     *
     * @param id
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void deleteMix(Long id) {
        TBusCargoMixRecordDTO cargoMixRecord = tBusCargoMixMapper.getMix(id);
        if (!"10".equals(cargoMixRecord.getStatus())) {
            throw new BusinessRuntimeException("当前混配已审核，无法删除");
        }

        tBusCargoMixMapper.deleteCargoMixRecord(cargoMixRecord.getId());
        tBusCargoMixMapper.deleteCargoMixDetail(cargoMixRecord.getId());
    }

    /**
     * 生成混配票货号
     *
     * @return
     */
    public String nextMixCargoInfoNo() {
        return commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.MAIN_CARGO_INFO, null);
    }

    /**
     * 生成混配新票货
     *
     * @param dto
     * @return
     */
    public TBusCargoInfoPO genMixCargoInfo(TBusCargoMixRecordDTO dto) {
        List<Long> cargoInfoIds = dto.getDetails().stream().map(TBusCargoMixDetailPO::getCargoInfoId).collect(Collectors.toList());
        List<TBusCargoInfoPO> cargoInfos = tBusCargoMixMapper.listCargoInfo(cargoInfoIds);
        BigDecimal mixWeight = dto.getDetails().stream().map(TBusCargoMixDetailPO::getMixWeight).reduce(BigDecimal.ZERO, BigDecimal::add);

        TBusCargoInfoPO newCargoInfo = new TBusCargoInfoPO();
        newCargoInfo.setId(snowflake.nextId());
        newCargoInfo.setShipvoyageId(null);
        newCargoInfo.setShipvoyageItemId(null);
        newCargoInfo.setScn(null);
        newCargoInfo.setShipName(null);
        newCargoInfo.setCargoInfoNo(nextMixCargoInfoNo());
        newCargoInfo.setCargoOwnerId(cargoInfos.get(0).getCargoOwnerId());
        newCargoInfo.setCargoOwnerName(cargoInfos.get(0).getCargoOwnerName());
        newCargoInfo.setCargoAgentId(null);
        newCargoInfo.setCargoAgentName(null);
        newCargoInfo.setTradeType(cargoInfos.get(0).getTradeType());
        newCargoInfo.setPackingCode(cargoInfos.get(0).getPackingCode());
        newCargoInfo.setPackingName(cargoInfos.get(0).getPackingName());
        newCargoInfo.setCompanyId(cargoInfos.get(0).getCompanyId());
        newCargoInfo.setCompanyName(cargoInfos.get(0).getCompanyName());
        newCargoInfo.setParentId(null);
        newCargoInfo.setRootId(null);
        newCargoInfo.setQuantity(null);
        newCargoInfo.setTon(mixWeight);
        newCargoInfo.setRightsQuantity(null);
        newCargoInfo.setSurplusRightsQuantity(mixWeight);
        newCargoInfo.setIsClear(CommonEnum.YesNoMode.NO.getCode());
        newCargoInfo.setClearBy(null);
        newCargoInfo.setClearByName(null);
        newCargoInfo.setClearDate(null);
        newCargoInfo.setSource("50");
        newCargoInfo.setStatementStatusCode(HandoverlistStatusEnum._10.getCode());
        newCargoInfo.setStatementStatusName(HandoverlistStatusEnum._10.getName());
        newCargoInfo.setTrustId(null);
        newCargoInfo.setContractItemId(null);
        newCargoInfo.setContractCode(null);
        newCargoInfo.setVoyage(null);
        newCargoInfo.setStorageDate(null);
        newCargoInfo.setResidueStorage(null);
        newCargoInfo.setTransferDate(null);
        newCargoInfo.setYsphh(null);
        newCargoInfo.setJszt(null);
        newCargoInfo.setTosCargoId(null);
        newCargoInfo.setCdh(null);
        newCargoInfo.setIsTos(null);
        newCargoInfo.setMixFreeStorageDays(dto.getDetails().stream().map(v1 -> v1.getMixWeight().multiply(BigDecimal.valueOf(v1.getRemainFreeStorageDays()))).reduce(BigDecimal.ZERO, BigDecimal::add).divide(mixWeight, 0, RoundingMode.HALF_UP).intValue());
        return newCargoInfo;
    }

    /**
     * 审核
     *
     * @param id
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void review(Long id) {
        TBusCargoMixRecordDTO dto = tBusCargoMixMapper.getMix(id);
        if (!"10".equals(dto.getStatus())) {
            throw new BusinessRuntimeException("当前混配已审核，无法再次审核");
        }



        //生成新票货写进票货表
        TBusCargoInfoPO newCargoInfo = genMixCargoInfo(dto);
        newCargoInfo.setCargoCode(dto.getCargoCode());
        newCargoInfo.setCargoName(tBusCargoMixMapper.getCargoNameByCode(dto.getCargoCode()));
        newCargoInfo.setVoyage(dto.getVoyage());
        newCargoInfo.setShipName(dto.getShipName());
        newCargoInfo.setShipvoyageId(dto.getShipvoyageId());
        newCargoInfo.setShipvoyageItemId(dto.getShipvoyageItemId());
        tBusCargoMixMapper.insertCargoInfo(newCargoInfo);

        Map<String, Object> dateAndShift = publicService.getDateAndShift(LocalDateTime.now());
        List<TPrdPortStorageDetailPO> details = dto.getDetails().stream().map(v1 -> {

            Map<String,Object> storageCargoStackTonMap= tBusCargoMixMapper.getStorageCargoStackTon(v1);
            if(CollectionUtils.isEmpty(storageCargoStackTonMap)){
                throw new BusinessRuntimeException("没有港存信息");
            }

            if(new BigDecimal(String.valueOf(storageCargoStackTonMap.get("ton"))).compareTo(v1.getMixWeight())<0){
                throw new BusinessRuntimeException("票货号："+String.valueOf(storageCargoStackTonMap.get("cargoInfoNo"))+
                        "</br>区域/垛位："+String.valueOf(storageCargoStackTonMap.get("regionName"))
                        +"/"+String.valueOf(storageCargoStackTonMap.get("massName"))+"港存不足");
            }

            TPrdPortStorageDetailPO detail = new TPrdPortStorageDetailPO();
            detail.setCargoInfoId(v1.getCargoInfoId());
            detail.setWorkDate(DateUtil.localDate2Date(DateUtil.str2LocalDate(String.valueOf(dateAndShift.get("workDate")))));
            detail.setClassCode(String.valueOf(dateAndShift.get("classCode")));
            detail.setClassName(String.valueOf(dateAndShift.get("className")));
            detail.setStorehouseId(v1.getStorehouseId());
            detail.setRegionId(v1.getRegionId());
            detail.setMassId(v1.getMassId());
            detail.setTon(v1.getMixWeight().multiply(BigDecimal.valueOf(-1)));
            detail.setInoutStorageCode(InoutStorageEnum._70.getCode());
            detail.setInoutStorageName(InoutStorageEnum._70.getLabel());
            detail.setInoutDate(DateUtil.localDate2Date(LocalDate.now()));
            detail.setCompanyId(newCargoInfo.getCompanyId());
            detail.setCompanyName(newCargoInfo.getCompanyName());
            detail.setCargoMixDetailId(v1.getId());
            return detail;
        }).collect(Collectors.toList());
        TPrdPortStorageDetailPO detail = new TPrdPortStorageDetailPO();
        detail.setCargoInfoId(newCargoInfo.getId());
        detail.setWorkDate(DateUtil.localDate2Date(DateUtil.str2LocalDate(String.valueOf(dateAndShift.get("workDate")))));
        detail.setClassCode(String.valueOf(dateAndShift.get("classCode")));
        detail.setClassName(String.valueOf(dateAndShift.get("className")));
        detail.setStorehouseId(dto.getStorehouseId());
        detail.setRegionId(dto.getRegionId());
        detail.setMassId(dto.getMassId());
        detail.setTon(dto.getMixWeight());
        detail.setInoutStorageCode(InoutStorageEnum._70.getCode());
        detail.setInoutStorageName(InoutStorageEnum._70.getLabel());
        detail.setInoutDate(DateUtil.localDate2Date(LocalDate.now()));
        detail.setCompanyId(newCargoInfo.getCompanyId());
        detail.setCompanyName(newCargoInfo.getCompanyName());
        detail.setCargoMixRecordId(dto.getId());
        detail.setProcessDetailName("混配");
        detail.setProcessDetailCode("10310001");

        details.add(detail);
        businessCommonService.insertPortStorageDetail(details);

        Map<Long, List<TBusCargoMixDetailPO>> groupByCargoInfoId = dto.getDetails().stream().collect(Collectors.groupingBy(TBusCargoMixDetailPO::getCargoInfoId));
        List<Map<String, Object>> cargoInfos = groupByCargoInfoId.entrySet().stream()
                .map(v1 -> new HashMap<String, Object>() {{
                    put("id", v1.getKey());
                    put("surplusRightsQuantity", v1.getValue().stream().map(TBusCargoMixDetailPO::getMixWeight).reduce(BigDecimal.ZERO, BigDecimal::add));
                }})
                .collect(Collectors.toList());
        tBusCargoMixMapper.updateCargoInfoSurplusRightsQuantity(cargoInfos);


        //生成混配杂项费
        if("1".equals(dto.getIsBilling())){
            //获取费率
            List<TBusRateDTO> rate = tBusCargoMixMapper.getBusRate(PROCSS_CD_HP);
            if(rate.isEmpty()){
                throw new BusinessRuntimeException("未找到对应的费率");
            }
            if(rate.size()>1){
                throw new BusinessRuntimeException("查询到多个作业过程为"+PROCSS_NM_HP+"的费率，无法自动生成混配杂项费用");
            }
            TMiscBillingPO tMiscBillingPO = new TMiscBillingPO();
            tMiscBillingPO.setId(snowflake.nextId());
            tMiscBillingPO.setCustomerId(dto.getCargoOwnerId());
            tMiscBillingPO.setCargoInfoId(newCargoInfo.getId());
            tMiscBillingPO.setIsCargoMix("1");
            tMiscBillingPO.setRateName(rate.get(0).getRateItemName());
            tMiscBillingPO.setRateItemCode(rate.get(0).getRateItemCode());
            tMiscBillingPO.setRate(rate.get(0).getRate());
            tMiscBillingPO.setTaxRate(rate.get(0).getTaxRate().multiply(new BigDecimal("100")));
            tMiscBillingPO.setStatus(10);

            //计算 金额、税额
            tMiscBillingPO.setAmountMoney(dto.getMixWeight().multiply(rate.get(0).getRate()).setScale(2,BigDecimal.ROUND_HALF_UP));
            tMiscBillingPO.setTaxAmount((tMiscBillingPO.getAmountMoney().multiply(rate.get(0).getTaxRate()).divide(BigDecimal.ONE.add(rate.get(0).getTaxRate()),2,BigDecimal.ROUND_HALF_UP)));
            tMiscBillingPO.setProcessCode(PROCSS_CD_HP);
            tMiscBillingPO.setProcessName(PROCSS_NM_HP);
            tMiscBillingPO.setShipVoyage(dto.getShipName()+"_"+dto.getVoyage());
            tMiscBillingPO.setVoyageId(dto.getShipvoyageItemId());
            tMiscBillingPO.setRateId(rate.get(0).getId());
            tMiscBillingPO.setUnitCode(rate.get(0).getMeasurementUnitCode1());
            tMiscBillingPO.setUnitName(rate.get(0).getMeasurementUnitName1());
            tMiscBillingPO.setBillQuantity(dto.getMixWeight());
            tMiscBillingPO.setBillDate(new Date());

            tBusCargoMixMapper.insertMiscBilling(tMiscBillingPO);
        }


        TBusCargoMixRecordPO po = new TBusCargoMixRecordPO();
        po.setId(id);
        po.setCargoInfoId(newCargoInfo.getId());
        po.setStatus("20");
        tBusCargoMixMapper.updateMixCargoInfoIdStatus(po);
    }

    /**
     * 销审
     *
     * @param id
     */
    @Override
    public void cancelReview(Long id) {
        TBusCargoMixRecordDTO dto = tBusCargoMixMapper.getMix(id);
        if (!"20".equals(dto.getStatus())) {
            throw new BusinessRuntimeException("当前混配未审核，无需销审");
        }

        TCostStorageSettlePO storageSettle = tBusCargoMixMapper.getStorageSettle(dto.getCargoInfoId());
        if (storageSettle != null) {
            throw new BusinessRuntimeException("当前混配票货已堆存费结算，无法销审");
        }

        List<TBusCargoInfoPO> tempCargoInfos = tBusCargoMixMapper.listTransferCargoInfo(dto.getCargoInfoId());
        if (!tempCargoInfos.isEmpty()) {
            throw new BusinessRuntimeException("当前混配票货已被货转，无法销审");
        }

        List<TBusTrustCargoPO> trustCargos = tBusCargoMixMapper.listTrustCargo(dto.getCargoInfoId());
        if (!trustCargos.isEmpty()) {
            throw new BusinessRuntimeException("当前混配票货已关联通知单，无法销审");
        }

        List<TBusCargoMixDetailPO> cargoMixDetails = tBusCargoMixMapper.listCargoMixDetail(dto.getCargoInfoId());
        if (!cargoMixDetails.isEmpty()) {
            throw new BusinessRuntimeException("当前混配票货已二次混配，无法销审");
        }

        //删除杂项费用
        TMiscBillingPO tMiscBillingPO = tBusCargoMixMapper.getMisnFee(dto.getCargoInfoId(),PROCSS_CD_HP);
        if(tMiscBillingPO!=null){
            if (tMiscBillingPO.getStatus()>10){
                throw new BusinessRuntimeException("混配杂项费用已经计费，请先撤销计费");
            }
            tBusCargoMixMapper.deleteMiscFee(tMiscBillingPO.getId(),tMiscBillingPO.getCargoInfoId());

        }

        Map<Long, List<TBusCargoMixDetailPO>> groupByCargoInfoId = dto.getDetails().stream().collect(Collectors.groupingBy(TBusCargoMixDetailPO::getCargoInfoId));
        List<Map<String, Object>> cargoInfos = groupByCargoInfoId.entrySet().stream()
                .map(v1 -> new HashMap<String, Object>() {{
                    put("id", v1.getKey());
                    put("surplusRightsQuantity", v1.getValue().stream().map(TBusCargoMixDetailPO::getMixWeight).reduce(BigDecimal.ZERO, BigDecimal::add).multiply(BigDecimal.valueOf(-1)));
                }})
                .collect(Collectors.toList());
        tBusCargoMixMapper.updateCargoInfoSurplusRightsQuantity(cargoInfos);

        List<Long> cargoMixRecordIds = dto.getDetails().stream().map(TBusCargoMixDetailPO::getId).collect(Collectors.toList());
        cargoMixRecordIds.add(dto.getId());
        List<TPrdPortStorageDetailPO> portStorageDetails = tBusCargoMixMapper.listPortStorageDetail(cargoMixRecordIds);
        if (!portStorageDetails.isEmpty()) {
            List<Long> portStorageDetailIds = portStorageDetails.stream().map(TPrdPortStorageDetailPO::getId).collect(Collectors.toList());
            businessCommonService.deletePortStorageDetail(portStorageDetailIds);
        }

        tBusCargoMixMapper.deleteCargoInfo(dto.getCargoInfoId());

        TBusCargoMixRecordPO cargoMixRecord = new TBusCargoMixRecordPO();
        cargoMixRecord.setId(id);
        cargoMixRecord.setCargoInfoId(null);
        cargoMixRecord.setStatus("10");
        tBusCargoMixMapper.updateMixCargoInfoIdStatus(cargoMixRecord);
    }
}
