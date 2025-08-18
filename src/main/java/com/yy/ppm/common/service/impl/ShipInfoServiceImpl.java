package com.yy.ppm.common.service.impl;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.DateUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.ppm.common.bean.dto.TDisPoundInfoDTO;
import com.yy.ppm.common.mapper.ShipInfoMapper;
import com.yy.ppm.common.service.ShipInfoService;
import com.yy.ppm.dispatch.bean.dto.TDisCostInfoPO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipDynamicDTO;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageQueryDTO;
import com.yy.ppm.dispatch.bean.po.TDisLowerCabinPO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyagePO;
import com.yy.ppm.dispatch.mapper.TDisShipDynamicMapper;
import com.yy.ppm.dispatch.mapper.TDisShipVoyageMapper;
import com.yy.ppm.produce.bean.dto.portStorage.InoutDetailQueryDTO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageDTO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageGbCargoInfoDTO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageQueryDTO;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.yy.ppm.common.enums.ShipStatusEnum.*;
import static com.yy.ppm.common.enums.ShipStatusEnum.DIMAO;

@Service
public class ShipInfoServiceImpl implements ShipInfoService {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(ShipInfoServiceImpl.class);

    @Resource
    private TDisShipVoyageMapper shipVoyageMapper;
    @Resource
    private TDisShipDynamicMapper shipDynamicMapper;


    @Resource
    private ShipInfoMapper shipInfoMapper;

    private static List<Map<String,Object>> STEP = new ArrayList<>();
    static {
        STEP.add(new HashMap<String,Object>(){{put("title","航次申报");put("statusCode",YUBAO.getCode());put("isExist",true);}});
        STEP.add(new HashMap<String,Object>(){{put("title","审核通过");put("statusCode",JIESHOU.getCode());put("isExist",true);}});
        STEP.add(new HashMap<String,Object>(){{put("title","抵锚");put("statusCode",DIMAO.getCode());put("isExist",true);}});
        STEP.add(new HashMap<String,Object>(){{put("title","靠泊");put("statusCode",KAOBO.getCode());put("isExist",true);}});
        STEP.add(new HashMap<String,Object>(){{put("title","开始作业");put("statusCode",KAIGONG.getCode());put("isExist",true);}});
        STEP.add(new HashMap<String,Object>(){{put("title","结束作业");put("statusCode",WANGONG.getCode());put("isExist",true);}});
        STEP.add(new HashMap<String,Object>(){{put("title","离泊");put("statusCode",LIBO.getCode());put("isExist",true);}});
        STEP.add(new HashMap<String,Object>(){{put("title","离港");put("statusCode",LIGANG.getCode());put("isExist",true);}});
    }

    @Override
    public Map<String, Object> getSteps(Long shipVoyageId) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> stepList = Lists.newArrayList();
        TDisShipvoyagePO shipvoyagePO = shipInfoMapper.getDisShipVoyage(shipVoyageId);
        List<TDisShipDynamicDTO> dynamicDTOS = shipInfoMapper.getByShipvoyageId(shipVoyageId);
        Map<String,List<TDisShipDynamicDTO>> dynamicMap = dynamicDTOS.stream().collect(Collectors.groupingBy(TDisShipDynamicDTO::getDynamicTypeCode));
        Integer shipStatusCode = Integer.valueOf(shipvoyagePO.getShipStatusCode());
        int stepIndex = 0;
        Boolean isDiMao = false;
        for (Map<String, Object> step : STEP) {
            Map<String, Object> map = Maps.newHashMap();
            String statusCode = String.valueOf(step.get("statusCode"));
            if(Integer.valueOf(statusCode)<=shipStatusCode){
                if(YUBAO.getCode().equals(statusCode)){//预报
                    map.put("title",step.get("title"));
                    map.put("date",DateUtils.formatDate(shipvoyagePO.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
                }
                else if(JIESHOU.getCode().equals(statusCode)){//接收
                    map.put("title",step.get("title"));
                    map.put("date",DateUtils.formatDate(shipvoyagePO.getReceiveTime(),"yyyy-MM-dd HH:mm:ss"));
                } else if(DIMAO.getCode().equals(statusCode)){
                    List<TDisShipDynamicDTO> dynamicList = dynamicMap.get(statusCode);
                    if(CollectionUtils.isNotEmpty(dynamicList)){
                        dynamicList = dynamicList.stream().sorted(Comparator.comparing(TDisShipDynamicDTO::getDynamicStartTime)).collect(Collectors.toList());
                        map.put("title",step.get("title"));
                        map.put("date",DateUtils.formatDate(dynamicList.get(0).getDynamicStartTime(),"yyyy-MM-dd HH:mm:ss"));
                        isDiMao = true;
                    }
                } else if(KAOBO.getCode().equals(statusCode)){
                    List<TDisShipDynamicDTO> dynamicList = dynamicMap.get(statusCode);
                    if(CollectionUtils.isNotEmpty(dynamicList)){
                        dynamicList = dynamicList.stream().sorted(Comparator.comparing(TDisShipDynamicDTO::getDynamicStartTime)).collect(Collectors.toList());
                        if(isDiMao){
                            map.put("title",step.get("title"));
                            map.put("date",DateUtils.formatDate(dynamicList.get(0).getDynamicStartTime(),"yyyy-MM-dd HH:mm:ss"));
                        }else{
                            map.put("title","直靠");
                            map.put("date",DateUtils.formatDate(dynamicList.get(0).getDynamicStartTime(),"yyyy-MM-dd HH:mm:ss"));
                        }
                    }
                } else if(KAIGONG.getCode().equals(statusCode)){
                    List<TDisShipDynamicDTO> dynamicList = dynamicMap.get(statusCode);
                    if(CollectionUtils.isNotEmpty(dynamicList)){
                        dynamicList = dynamicList.stream().sorted(Comparator.comparing(TDisShipDynamicDTO::getDynamicStartTime)).collect(Collectors.toList());
                        map.put("title",step.get("title"));
                        map.put("date",DateUtils.formatDate(dynamicList.get(0).getDynamicStartTime(),"yyyy-MM-dd HH:mm:ss"));
                    }
                } else if(WANGONG.getCode().equals(statusCode)){
                    List<TDisShipDynamicDTO> dynamicList = dynamicMap.get(statusCode);
                    if(CollectionUtils.isNotEmpty(dynamicList)){
                        dynamicList = dynamicList.stream().sorted(Comparator.comparing(TDisShipDynamicDTO::getDynamicStartTime).reversed()).collect(Collectors.toList());
                        map.put("title",step.get("title"));
                        map.put("date",DateUtils.formatDate(dynamicList.get(0).getDynamicStartTime(),"yyyy-MM-dd HH:mm:ss"));
                    }
                } else if(LIBO.getCode().equals(statusCode)){
                    List<TDisShipDynamicDTO> dynamicList = dynamicMap.get(statusCode);
                    if(CollectionUtils.isNotEmpty(dynamicList)){
                        dynamicList = dynamicList.stream().sorted(Comparator.comparing(TDisShipDynamicDTO::getDynamicStartTime).reversed()).collect(Collectors.toList());
                        map.put("title",step.get("title"));
                        map.put("date",DateUtils.formatDate(dynamicList.get(0).getDynamicStartTime(),"yyyy-MM-dd HH:mm:ss"));
                    }
                }
                else{
                    List<TDisShipDynamicDTO> dynamicList = dynamicMap.get(statusCode);
                    if(CollectionUtils.isNotEmpty(dynamicList)){
                        map.put("title",step.get("title"));
                        map.put("date",DateUtils.formatDate(dynamicList.get(0).getDynamicStartTime(),"yyyy-MM-dd HH:mm:ss"));
                    }
                }
                stepIndex++;
            }else{
                map.put("title",step.get("title"));
                map.put("date","");
            }
            stepList.add(map);
        }
        result.put("stepIndex",stepIndex);
        result.put("stepList",stepList);
        return result;
    }

    @Override
    public TDisShipvoyageDTO getShipVoyageInfo(Long shipVoyageId) {
        TDisShipvoyagePO po = shipInfoMapper.getDisShipVoyage(shipVoyageId);
        TDisShipvoyageQueryDTO queryDTO = new TDisShipvoyageQueryDTO();
        queryDTO.setShipStatusCode(po.getShipStatusCode());
        queryDTO.setId(shipVoyageId);
        List<TDisShipvoyageDTO> page = shipInfoMapper.listDisShipVoyage(queryDTO);
        if(CollectionUtils.isNotEmpty(page)){
            return page.get(0);
        }
        return null;
    }

    @Override
    public Map<String,Object> getShipDynamicInfo(Long shipVoyageId) {
        Map<String,Object> result = Maps.newHashMap();
        List<TDisShipDynamicDTO> allDynamicDTOS = shipInfoMapper.getByShipvoyageId(shipVoyageId);
        List<TDisShipDynamicDTO> inDynamicDTOS = shipInfoMapper.getTFByShipvoyageId(shipVoyageId,"IN");
        List<TDisShipDynamicDTO> outDynamicDTOS = shipInfoMapper.getTFByShipvoyageId(shipVoyageId,"OUT");
        if(CollectionUtils.isNotEmpty(inDynamicDTOS)){
//            allDynamicDTOS.addAll(inDynamicDTOS);
            Map<String,Map<String,Object>> tempMap = Maps.newHashMap();
            BigDecimal sumStopTime = BigDecimal.ZERO;
            for(int i=0;i<inDynamicDTOS.size();i++){
                TDisShipDynamicDTO dto = inDynamicDTOS.get(i);
                String dynamicTypeCode = dto.getDynamicTypeCode();
                if("90".equals(dynamicTypeCode)){
                    if(StringUtil.isNotEmpty(dto.getStopTimeLen())){
                        TDisShipDynamicDTO preDto = inDynamicDTOS.get(i-1);
                        String stopTypeCode = preDto.getStopTypeCode();
                        Map<String,Object> map = tempMap.get(stopTypeCode);
                        BigDecimal stopTimeLen = new BigDecimal(dto.getStopTimeLen());
                        sumStopTime = sumStopTime.add(stopTimeLen);
                        if(map!=null && !map.isEmpty()){
                            BigDecimal value = (BigDecimal) map.get("value");
                            value = value.add(stopTimeLen);
                            map.put("value",value);
                        }else{
                            map = Maps.newHashMap();
                            String stopTypeName = preDto.getStopTypeName();
                            map.put("name",stopTypeName);
                            map.put("value",stopTimeLen);
                            tempMap.put(stopTypeCode,map);
                        }
                    }
                }
            }
            setPercent(tempMap.values(),sumStopTime);
            result.put("inData",tempMap.values());
            result.put("inStopTime", sumStopTime);
        }
        if(CollectionUtils.isNotEmpty(outDynamicDTOS)){
//            allDynamicDTOS.addAll(outDynamicDTOS);
            Map<String,Map<String,Object>> tempMap = Maps.newHashMap();
            BigDecimal sumStopTime = BigDecimal.ZERO;
//            outDynamicDTOS = outDynamicDTOS.stream().sorted(Comparator.comparing(TDisShipDynamicDTO::getDynamicStartTime)).collect(Collectors.toList());
            for(int i=0;i<outDynamicDTOS.size();i++){
                TDisShipDynamicDTO dto = outDynamicDTOS.get(i);
                String dynamicTypeCode = dto.getDynamicTypeCode();
                if("90".equals(dynamicTypeCode)){
                    if(StringUtil.isNotEmpty(dto.getStopTimeLen())){
                        TDisShipDynamicDTO preDto = outDynamicDTOS.get(i-1);
                        String stopTypeCode = preDto.getStopTypeCode();
                        Map<String,Object> map = tempMap.get(stopTypeCode);
                        BigDecimal stopTimeLen = new BigDecimal(dto.getStopTimeLen());
                        sumStopTime = sumStopTime.add(stopTimeLen);
                        if(map!=null && !map.isEmpty()){
                            BigDecimal value = (BigDecimal) map.get("value");
                            value = value.add(stopTimeLen);
                            map.put("value",value);
                        }else{
                            map = Maps.newHashMap();
                            String stopTypeName = preDto.getStopTypeName();
                            map.put("name",stopTypeName);
                            map.put("value",stopTimeLen);
                            tempMap.put(stopTypeCode,map);
                        }
                    }
                }
            }
            setPercent(tempMap.values(),sumStopTime);
            result.put("outData",tempMap.values());
            result.put("outStopTime", sumStopTime);
        }
        result.put("dynamicDTOS",allDynamicDTOS);
        return result;
    }

    @Override
    public List<TDisLowerCabinPO> getShipDoorInfo(TDisLowerCabinPO tDisLowerCabinPO) {
        return shipInfoMapper.getgetShipDoorInfo(tDisLowerCabinPO);
    }

    private void setPercent(Collection<Map<String,Object>> list,BigDecimal sumStopTime){
        list.forEach(map->{
            BigDecimal value = new BigDecimal(String.valueOf(map.get("value")));
            if(BigDecimal.ZERO.equals(sumStopTime)){
                map.put("percent",0);
            }else{
                BigDecimal percent = value.multiply(new BigDecimal(100)).divide(sumStopTime,2,BigDecimal.ROUND_HALF_UP);
                map.put("percent",percent);
            }
        });
    }



    @Override
    public List<TPrdPortStorageGbCargoInfoDTO> getPortTrendsInfo(Long shipVoyageId) {
        TPrdPortStorageQueryDTO queryDTO = new TPrdPortStorageQueryDTO();
        queryDTO.setShipVoyageId(shipVoyageId);
        List<TPrdPortStorageGbCargoInfoDTO> list = shipInfoMapper.listPortStorageGbCargoInfo(queryDTO);
        for (TPrdPortStorageGbCargoInfoDTO dto : list) {
            dto.setTable(listPortStorage(dto.getCargoInfoNo()));
        }
        return list;
    }

    @Override
    public List<TPrdPortStorageDTO> listPortStorage(String cargoInfoNo) {
        TPrdPortStorageQueryDTO query = new TPrdPortStorageQueryDTO();
        query.setCargoInfoNo(cargoInfoNo);
        List<TPrdPortStorageDTO> list = shipInfoMapper.listPortStorage(query);
        return list;
    }


    @Override
    public Map<String, Object> getInoutDetail(InoutDetailQueryDTO query) {
        List<TPrdPortStorageDetailPO> portStorageDetails = shipInfoMapper.listPortStorageDetail(
                query.getCargoInfoId(), query.getStorehouseId(), query.getRegionId(), query.getMassId(), query.getBeginWorkDate()
                , query.getBeginClassCode(), query.getEndWorkDate(), query.getEndClassCode(), query.getProcessDetailCode()
        );

        Map<Boolean, List<TPrdPortStorageDetailPO>> groupByCompareToZero = portStorageDetails.stream()
                .collect(Collectors.groupingBy(v1 -> v1.getTon().compareTo(BigDecimal.ZERO) > 0));

        List<TPrdPortStorageDetailPO> in = Optional.ofNullable(groupByCompareToZero.get(true)).orElse(Collections.emptyList());
        List<TPrdPortStorageDetailPO> out = Optional.ofNullable(groupByCompareToZero.get(false)).orElse(Collections.emptyList()).stream().peek(v1 -> {
            if (v1.getQuantity() != null) {
                v1.setQuantity(v1.getQuantity() * -1);
            }
            if (v1.getTon() != null) {
                v1.setTon(v1.getTon().multiply(BigDecimal.valueOf(-1)));
            }
        }).collect(Collectors.toList());

        Integer inQuantity = null;
        if (!in.stream().allMatch(v1 -> v1.getQuantity() == null)) {
            inQuantity = in.stream().mapToInt(v1 -> Optional.ofNullable(v1.getQuantity()).orElse(0)).sum();
        }
        BigDecimal inTon = in.stream().map(TPrdPortStorageDetailPO::getTon).reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer outQuantity = null;
        if (!out.stream().allMatch(v1 -> v1.getQuantity() == null)) {
            outQuantity = out.stream().mapToInt(v1 -> Optional.ofNullable(v1.getQuantity()).orElse(0)).sum();
        }
        BigDecimal outTon = out.stream().map(TPrdPortStorageDetailPO::getTon).reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer balanceQuantity = null;
        if (!(inQuantity == null && outQuantity == null)) {
            balanceQuantity = Optional.ofNullable(inQuantity).orElse(0) - Optional.ofNullable(outQuantity).orElse(0);
        }
        BigDecimal balanceTon = inTon.subtract(outTon);

        HashMap<String, Object> result = new HashMap<>();
        result.put("in", in);
        result.put("inQuantity", inQuantity);
        result.put("inTon", inTon);
        result.put("out", out);
        result.put("outQuantity", outQuantity);
        result.put("outTon", outTon);
        result.put("balanceQuantity", balanceQuantity);
        result.put("balanceTon", balanceTon);
        return result;
    }

    @Override
    public List<TDisCostInfoPO> getCostInfo(Long shipVoyageId) {
        return shipInfoMapper.getCostInfo(shipVoyageId);
    }

    @Override
    public List<TDisPoundInfoDTO> getPoundInfo(Long shipVoyageId) {
        List<String> cargoInfoNo = shipInfoMapper.getCargoInfoNoByShipVoyageId(shipVoyageId);
        List<TDisPoundInfoDTO> list = shipInfoMapper.getPoundInfo(cargoInfoNo);
        return list;
    }
}
