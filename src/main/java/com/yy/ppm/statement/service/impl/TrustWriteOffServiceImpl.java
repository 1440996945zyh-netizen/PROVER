package com.yy.ppm.statement.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusCargoInfoDTO;
import com.yy.ppm.business.bean.dto.TBusTrustDTO;
import com.yy.ppm.business.bean.dto.TBusTrustSearchDTO;
import com.yy.ppm.business.bean.po.TBusRatePO;
import com.yy.ppm.business.bean.po.TBusTrustPO;
import com.yy.ppm.common.enums.BusTrustStatusEnum;
import com.yy.ppm.statement.bean.dto.TMiscBillingDTO;
import com.yy.ppm.statement.bean.po.TMiscBillingPO;
import com.yy.ppm.statement.mapper.MiscBillingMapper;
import com.yy.ppm.statement.mapper.TrustWriteOffMapper;
import com.yy.ppm.statement.service.TrustWriteOffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-10-11 13:49
 */
@Service
public class TrustWriteOffServiceImpl implements TrustWriteOffService {

    @Resource
    private TrustWriteOffMapper trustWriteOffMapper;

    @Autowired
    private Snowflake snowflake;

    @Resource
    private MiscBillingMapper miscBillingMapper;

    @Override
    public Pages<TBusTrustDTO> getWriteOffList(TBusTrustSearchDTO searchDTO) {
        Pages<TBusTrustDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return trustWriteOffMapper.getWriteOffList(searchDTO);
        });

        List<Long> trustIds = pages.getPages().stream().filter(v1 -> v1.getShipvoyageItemId() == null).map(TBusTrustPO::getId).collect(Collectors.toList());
        if (!trustIds.isEmpty()) {
            List<Map<String, Object>> shipvoyageItems = trustWriteOffMapper.listShipvoyageItemByTrustIds(trustIds);
            pages.getPages()
                    .stream().filter(v1 -> trustIds.contains(v1.getId()))
                    .forEach(v1 -> {
                        String shipNameVoyages = shipvoyageItems.stream()
                                .filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                .map(v2 -> String.valueOf(v2.get("shipNameVoyage")))
                                .collect(Collectors.joining("，"));
                        v1.setShipNameVoyage(shipNameVoyages);
                    });
        }

        return pages;
    }

    @Override
    public Map<String, Object> getWriteOffById(Long trustId) {
        Map<String, Object> result = new HashMap<>();
        TBusTrustPO trust = trustWriteOffMapper.getTrust(trustId);
        boolean anyMatch = Stream.of("船舶加水", "接电").anyMatch(v1 -> v1.equals(trust.getProcessName()));
        boolean anyMatch2 = Stream.of("货物加水").anyMatch(v1 -> v1.equals(trust.getProcessName()));
        if (anyMatch) {
            List<Map<String, Object>> waterElectricitys = trustWriteOffMapper.listWaterElectricity(trustId);
            result.put("flag", "0");
            result.put("list", waterElectricitys);
        }else if(anyMatch2){
            List<Map<String, Object>> waterElectricitys = trustWriteOffMapper.listCargoWater(trustId);
            result.put("flag", "0");
            result.put("list", waterElectricitys);
        } else {
            List<Map<String, Object>> workTicketDetails = trustWriteOffMapper.getWriteOffById(trustId);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            workTicketDetails.forEach(map -> {
                String date = format.format(map.get("workDate"));
                map.put("workDate", date);
            });
            result.put("flag", "1");
            result.put("list", workTicketDetails);
        }
        return result;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void writeOff(Long trustId, Integer checkNumber, BigDecimal checkTon) {
        if (trustId == null) {
            throw new BusinessRuntimeException("错误的指令ID");
        }

        TBusTrustPO trust = trustWriteOffMapper.getTrust(trustId);
        if (BusTrustStatusEnum.HX.getCode().equals(trust.getStatus())) {
            throw new BusinessRuntimeException("核销失败，当前指令已核销");
        }

        TBusTrustPO po = new TBusTrustPO();
        po.setId(trustId);
        po.setCheckNumber(checkNumber);
        po.setCheckTon(checkTon);
        trustWriteOffMapper.writeOff(po);

        if ("杂项".equals(trust.getType())) {
            if ("1".equals(trust.getIsBill())) {

                if(Stream.of("船舶接电(非作业)","船舶加水(非作业)").anyMatch(v1 -> v1.equals(trust.getProcessName()))){
                    // 杂项计费匹配费率，写入杂项计费
                    List<TBusRatePO> rates = trustWriteOffMapper.listRate(trust.getProcessCode());
                    if (rates.isEmpty()) {
                        throw new BusinessRuntimeException("核销失败，杂项计费指令未匹配到费率");
                    }

                    for (TBusRatePO rate : rates) {
                        if (rate.getId() == null) {
                            throw new BusinessRuntimeException("费率id为空");
                        }

                        List<TMiscBillingDTO> processByRateId = miscBillingMapper.getProcessByRateId(String.valueOf(rate.getId()));

                        TMiscBillingPO miscBilling = new TMiscBillingPO();
                        miscBilling.setId(snowflake.nextId());
                        miscBilling.setRateItemCode(rate.getRateItemCode());
                        miscBilling.setRateName(rate.getRateItemName());
                        miscBilling.setRate(rate.getRate());
                        miscBilling.setTaxRate(rate.getTaxRate());
                        miscBilling.setBillDate(new Date());
                        miscBilling.setUnitCode(!processByRateId.isEmpty() ? processByRateId.get(0).getUnitCode() : null);
                        miscBilling.setUnitName(!processByRateId.isEmpty() ? processByRateId.get(0).getUnitName() : null);
                        miscBilling.setVoyageId(trust.getShipvoyageItemId());
                        miscBilling.setShipVoyage(trust.getShipName());
                        miscBilling.setBillQuantity(checkTon);
                        miscBilling.setAmountMoney(rate.getRate().multiply(checkTon));
                        miscBilling.setTaxAmount(miscBilling.getAmountMoney().multiply(rate.getTaxRate().multiply(BigDecimal.valueOf(0.01))).divide(BigDecimal.ONE.add(rate.getTaxRate().multiply(BigDecimal.valueOf(0.01))), 2, RoundingMode.HALF_UP));
                        miscBilling.setTrustOrderId(trustId);
                        miscBilling.setCustomerId(trust.getCustomerId());
                        miscBilling.setStatus("0".equals(rate.getIsMainIncome())?20:10);
                        miscBilling.setCompanyId(trust.getCompanyId());
                        miscBilling.setCompanyName(trust.getCompanyName());
                        miscBilling.setRateId(rate.getId());
                        miscBilling.setProcessCode(!processByRateId.isEmpty() ? processByRateId.get(0).getProcessCode() : null);
                        miscBilling.setProcessName(!processByRateId.isEmpty() ? processByRateId.get(0).getProcessName() : null);

                        List<TBusCargoInfoDTO> cargoTrustList = trustWriteOffMapper.getCargoByTrust(trustId);
                        if (!CollectionUtils.isEmpty(cargoTrustList)) { // 判断是否有 唯一 的票货，如果有则存入杂项计费表
                            if (cargoTrustList.size() > 1) {
                                throw new BusinessRuntimeException("该杂项计划存在多个票货，无法计费");
                            }
                            miscBilling.setCargoInfoId(cargoTrustList.get(0).getId());
                            miscBilling.setCargoInfoName(cargoTrustList.get(0).getCargoInfoName());
                        }

                        trustWriteOffMapper.insertMiscBilling(miscBilling);
                    }

                }else{
                    // 杂项计费匹配费率，写入杂项计费
                    boolean anyMatch = Stream.of("船舶加水", "接电").anyMatch(v1 -> v1.equals(trust.getProcessName()));
                    if (anyMatch) {
                        // 船舶加水、接电不参与杂项计费
                        return;
                    }
                    List<TBusRatePO> rates = trustWriteOffMapper.listRate(trust.getProcessCode());
                    if (rates.isEmpty()) {
                        throw new BusinessRuntimeException("核销失败，杂项计费指令未匹配到费率");
                    }
                    if (rates.size() > 1) {
                        throw new BusinessRuntimeException("核销失败，杂项计费指令匹配到多条费率");
                    }

                    TBusRatePO rate = rates.get(0);

                    if(rates.get(0).getId() == null) {
                        throw new BusinessRuntimeException("费率id为空");
                    }
                    List<TMiscBillingDTO> processByRateId = miscBillingMapper.getProcessByRateId(String.valueOf(rates.get(0).getId()));

                    TMiscBillingPO miscBilling = new TMiscBillingPO();
                    miscBilling.setId(snowflake.nextId());
                    miscBilling.setRateItemCode(rate.getRateItemCode());
                    miscBilling.setRateName(rate.getRateItemName());
                    miscBilling.setRate(rate.getRate());
                    miscBilling.setTaxRate(rate.getTaxRate());
                    miscBilling.setBillDate(new Date());
                    miscBilling.setUnitCode(!processByRateId.isEmpty() ? processByRateId.get(0).getUnitCode() : null);
                    miscBilling.setUnitName(!processByRateId.isEmpty() ? processByRateId.get(0).getUnitName() : null);
                    miscBilling.setVoyageId(trust.getShipvoyageItemId());
                    miscBilling.setShipVoyage(trust.getShipName());
                    miscBilling.setBillQuantity(checkTon);
                    miscBilling.setAmountMoney(rate.getRate().multiply(checkTon));
                    miscBilling.setTaxAmount(miscBilling.getAmountMoney().multiply(rate.getTaxRate().multiply(BigDecimal.valueOf(0.01))).divide(BigDecimal.ONE.add(rate.getTaxRate().multiply(BigDecimal.valueOf(0.01))), 2, RoundingMode.HALF_UP));
                    miscBilling.setTrustOrderId(trustId);
                    miscBilling.setCustomerId(trust.getCustomerId());
                    miscBilling.setStatus(10);
                    miscBilling.setCompanyId(trust.getCompanyId());
                    miscBilling.setCompanyName(trust.getCompanyName());
                    miscBilling.setRateId(rate.getId());
                    miscBilling.setProcessCode(!processByRateId.isEmpty() ? processByRateId.get(0).getProcessCode() : null);
                    miscBilling.setProcessName(!processByRateId.isEmpty() ? processByRateId.get(0).getProcessName() : null);
                    List<TBusCargoInfoDTO> cargoTrustList = trustWriteOffMapper.getCargoByTrust(trustId);
                    if (!CollectionUtils.isEmpty(cargoTrustList)) { // 判断是否有 唯一 的票货，如果有则存入杂项计费表
                        if (cargoTrustList.size() > 1) {
                            throw new BusinessRuntimeException("该杂项计划存在多个票货，无法计费");
                        }
                        miscBilling.setCargoInfoId(cargoTrustList.get(0).getId());
                        miscBilling.setCargoInfoName(cargoTrustList.get(0).getCargoInfoName());
                    }
                    trustWriteOffMapper.insertMiscBilling(miscBilling);
                }

            }
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void cancelWriteOff(Long trustId) {
        if (trustId == null) {
            throw new BusinessRuntimeException("错误的指令ID");
        }

        TBusTrustPO trust = trustWriteOffMapper.getTrust(trustId);
        if (!BusTrustStatusEnum.HX.getCode().equals(trust.getStatus())) {
            throw new BusinessRuntimeException("取消核销失败，当前指令未核销");
        }

        List<TMiscBillingDTO> billingList = trustWriteOffMapper.getBillingListByTrustId(trustId);
        if (!CollectionUtils.isEmpty(billingList)) {
            billingList.stream().forEach(item -> {
                if (item.getStatus() >= 30) {
                    throw new BusinessRuntimeException("取消核销失败，当前指令存在已审核计费");
                }
                miscBillingMapper.deleteMisc(item.getId());
            });
        }
        TBusTrustPO po = new TBusTrustPO();
        po.setId(trustId);
        trustWriteOffMapper.cancelWriteOff(po);
    }
}
