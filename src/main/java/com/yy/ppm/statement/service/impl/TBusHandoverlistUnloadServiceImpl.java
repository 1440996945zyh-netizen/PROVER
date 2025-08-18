package com.yy.ppm.statement.service.impl;


import cn.hutool.core.lang.Snowflake;
import com.github.pagehelper.Page;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusHandoverListDTO;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import com.yy.ppm.business.bean.po.TBusTrustPO;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.common.enums.BusHandoverlistTypeEnum;
import com.yy.ppm.common.enums.HandoverlistStatusEnum;
import com.yy.ppm.common.enums.ShipStatusEnum;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.statement.bean.dto.busHandoverlist.*;
import com.yy.ppm.statement.bean.dto.storageSettle.VWeightInfo;
import com.yy.ppm.statement.bean.po.TBusHandoverlistPO;
import com.yy.ppm.statement.mapper.TBusHandoverlistMapper;
import com.yy.ppm.statement.mapper.TBusHandoverlistUnloadMapper;
import com.yy.ppm.statement.service.TBusHandoverlistService;
import com.yy.ppm.statement.service.TBusHandoverlistUnloadService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-07 10:58
 */
@Service
public class TBusHandoverlistUnloadServiceImpl implements TBusHandoverlistUnloadService {

    @Autowired
    private TBusHandoverlistUnloadMapper tBusHandoverlistMapper;

    @Autowired
    private Snowflake snowflake;

    @Resource
    private SysFileService sysFileService;

    @Autowired
    private CommonService commonService;

    private static final String START_WORK = "1";

    @Override
    public Pages<TDisShipvoyageItemDTO> listDisShipvoyageItem(TDisShipvoyageItemQueryDTO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            query.setIsStartWork(START_WORK);
            Page<TDisShipvoyageItemDTO> tDisShipvoyageItemDTOS = tBusHandoverlistMapper.listDisShipvoyageItem(query);

            tDisShipvoyageItemDTOS.forEach(x -> {
                x.setHandoverlistTon(tBusHandoverlistMapper.listBusHandoverlistSumTon(x.getShipvoyageItemId()) != null ? tBusHandoverlistMapper.listBusHandoverlistSumTon(x.getShipvoyageItemId()) : BigDecimal.ZERO);
                List<Long> fileidList = tBusHandoverlistMapper.getFileIdByShipvoyageItemId(x.getShipvoyageItemId());
                if(!CollectionUtils.isEmpty(fileidList)){
                    x.setFileIds(fileidList);
                }else{
                    x.setFileIds(new ArrayList<>());
                }
            });


            return tDisShipvoyageItemDTOS;
        });
    }

    @Override
    public Pages<TBusTrustDTO> listTrust(TBusTrustQueryDTO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            return tBusHandoverlistMapper.listTrust(query);
        });
    }

    @Override
    public List<TBusHandoverlistDTO> listBusHandoverlist(Long shipvoyageItemId, Long trustId) {
        List<TBusHandoverlistDTO> tBusHandoverlistDTOS = tBusHandoverlistMapper.listBusHandoverlist(shipvoyageItemId, trustId);
        for (TBusHandoverlistDTO tBusHandoverlistDTO : tBusHandoverlistDTOS) {
            List<SysFileDTO> fileInfo = sysFileService.getBusFiles(tBusHandoverlistDTO.getShipvoyageItemId(), "HANDOVERLIST");
            tBusHandoverlistDTO.setMattachmentInfoList(fileInfo);
//            if(tBusHandoverlistDTO.getQuantity() == null && (tBusHandoverlistDTO.getTon() == null || tBusHandoverlistDTO.getTon().compareTo(BigDecimal.ZERO) == 0)){
//                //没有填写交接清单,根据航次ID，票货ID查询汇总理货量
//               Map<String,Object> map = tBusHandoverlistMapper.getTallyMeasure(tBusHandoverlistDTO);
//               if(map != null){
//                   if(map.get("quantity") != null){
//                       tBusHandoverlistDTO.setQuantity(Integer.parseInt(map.get("quantity").toString()));
//                   }
//                   if(map.get("ton") != null){
//                       tBusHandoverlistDTO.setTon(BigDecimal.valueOf(Double.parseDouble(map.get("ton").toString())));
//                   }
//               }
//
//            }
        }
        return tBusHandoverlistDTOS;
    }

    @Override
    public List<TBusCargoInfoDTO> listBusCargoInfo(Long shipvoyageItemId, Long trustId) {
        List<TBusCargoInfoDTO> resList = tBusHandoverlistMapper.listBusCargoInfo(shipvoyageItemId, trustId);
        for (TBusCargoInfoDTO pos : resList) {
            if(trustId!=null){
                pos.setTrustCargoId(tBusHandoverlistMapper.getTrustCargoId(pos.getId(),trustId));
            }
            pos.setQuantity(0);
            pos.setTon(new BigDecimal(0));
            //没有填写交接清单,根据航次ID，票货ID查询汇总理货量
            Map<String, Object> map = tBusHandoverlistMapper.getTallyMeasure(pos.getId(), shipvoyageItemId);
            if (map != null) {
                if (map.get("quantity") != null) {
                    pos.setQuantity(Integer.parseInt(map.get("quantity").toString()));
                }
                if (map.get("ton") != null) {
                    pos.setTon(BigDecimal.valueOf(Double.parseDouble(map.get("ton").toString())));
                }
            }
            //集港过磅量
            VWeightInfo VJInfo= tBusHandoverlistMapper.getJGWeightTonByCargoInfo(pos.getId());
            pos.setJGweightGoods(VJInfo==null?null:VJInfo.getWeightGoods());
            //疏港过磅量
            VWeightInfo VSInfo= tBusHandoverlistMapper.getSGWeightTonByCargoInfo(pos.getId());
            pos.setSGweightGoods(VSInfo==null?null:VSInfo.getWeightGoods());
        }

        // 查询交接清单数量，如果交接清单不为空--add by zcc 2023/11/28
        List<TBusHandoverlistPO> busHandoverlist = tBusHandoverlistMapper.getBusHandoverlist(shipvoyageItemId, trustId);
        if (CollectionUtils.isNotEmpty(busHandoverlist) && CollectionUtils.isNotEmpty(resList)) {
            for (TBusCargoInfoDTO tBusCargoInfoDTO : resList) {
                for (TBusHandoverlistPO busHandoverlistPO : busHandoverlist) {
                    if (busHandoverlistPO.getCargoInfoId().equals(tBusCargoInfoDTO.getId())) {
                        tBusCargoInfoDTO.setTon(busHandoverlistPO.getTon());
                        tBusCargoInfoDTO.setQuantity(busHandoverlistPO.getQuantity());
                        tBusCargoInfoDTO.setTicketNum(busHandoverlistPO.getTicketNum());
                    }
                }
            }
        }
        return resList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public StringBuffer updateBusHandoverlist(UpdateBusHandoverlistDTO dto) {
        StringBuffer cargoNos = new StringBuffer();
        if (!dto.getHandoverlists().isEmpty() && BusHandoverlistTypeEnum.ZHUANGXIECHUAN.getCode().equals(dto.getHandoverlists().get(0).getType())) {
            tBusHandoverlistMapper.deleteFileBusiness(dto.getHandoverlists().get(0).getShipvoyageItemId());

            if (CollectionUtils.isNotEmpty(dto.getFileIds())) {
                // 图片附件
                List<Map<String, Long>> files = dto.getFileIds().stream().map(v1 -> new HashMap<String, Long>() {{
                    put("businessId", dto.getHandoverlists().get(0).getShipvoyageItemId());
                    put("fileId", v1);
                }}).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(dto.getFileIds())) {
                    tBusHandoverlistMapper.insertFileBusiness(files);
                }
            }
            if (CollectionUtils.isNotEmpty(dto.getHgFileIds())) {
                // 海关报关单附件
                List<Map<String, Long>> hgFiles = dto.getHgFileIds().stream().map(v1 -> new HashMap<String, Long>() {{
                    put("businessId", dto.getHandoverlists().get(0).getShipvoyageItemId());
                    put("fileId", v1);
                }}).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(dto.getHgFileIds())) {
                    tBusHandoverlistMapper.insertFileBusiness(hgFiles);
                }
            }
            if (CollectionUtils.isNotEmpty(dto.getJcFileIds())) {
                // 第三方检测报告附件
                List<Map<String, Long>> jcFiles = dto.getJcFileIds().stream().map(v1 -> new HashMap<String, Long>() {{
                    put("businessId", dto.getHandoverlists().get(0).getShipvoyageItemId());
                    put("fileId", v1);
                }}).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(dto.getJcFileIds())) {
                    tBusHandoverlistMapper.insertFileBusiness(jcFiles);
                }
            }
        }

        // 判断交接清单是否已经结算 update by zcc 2023/11/30
        if (!dto.getHandoverlists().isEmpty()) {
            List<TBusHandoverlistPO> insertList = new ArrayList<>();
            List<TBusHandoverlistPO> updateList = new ArrayList<>();
            for (TBusHandoverlistPO busHandoverlistPO : dto.getHandoverlists()) {
                Long id = busHandoverlistPO.getId();
                int checkCount = tBusHandoverlistMapper.checkCostStatementCount(id);
                if (checkCount <= 0) {
                    //tBusHandoverlistMapper.deleteBusHandoverlist(id);
                    TBusHandoverlistPO busHandoverListById = tBusHandoverlistMapper.getBusHandoverListById(id);
                    busHandoverlistPO.setStatementStatusCode(HandoverlistStatusEnum._10.getCode());
                    busHandoverlistPO.setStatementStatusName(HandoverlistStatusEnum._10.getName());
                    if (busHandoverListById != null) {
                        int count = 0;
                        if ((busHandoverListById.getQuantity() != null && busHandoverlistPO.getQuantity() != null &&
                                !busHandoverListById.getQuantity().equals(busHandoverlistPO.getQuantity())) ||
                                (busHandoverListById.getTon() != null && busHandoverlistPO.getTon() != null &&
                                        busHandoverListById.getTon().compareTo(busHandoverlistPO.getTon()) != 0) ||
                                (busHandoverListById.getVolumeTon() != null && busHandoverlistPO.getVolumeTon() != null &&
                                        busHandoverListById.getVolumeTon().compareTo(busHandoverlistPO.getVolumeTon()) != 0) ||
                                (busHandoverListById.getTicketNum() != null && busHandoverlistPO.getTicketNum() != null &&
                                        !busHandoverListById.getTicketNum().equals(busHandoverlistPO.getTicketNum())) ||
                                (busHandoverListById.getRemark() != null && busHandoverlistPO.getRemark() != null &&
                                        !busHandoverListById.getRemark().equals(busHandoverlistPO.getRemark())) ||
                                (busHandoverListById.getDeliveryNumbers() != null && busHandoverlistPO.getDeliveryNumbers() != null &&
                                        !busHandoverListById.getDeliveryNumbers().equals(busHandoverlistPO.getDeliveryNumbers()))) {
                            count = tBusHandoverlistMapper.getTrustTypeByCargoInfoNo(busHandoverlistPO.getCargoInfoNo());
                        }
                        if (count > 0) {
                            cargoNos.append(busHandoverlistPO.getCargoInfoNo() + ",");
                        } else {
                            updateList.add(busHandoverlistPO);
                        }

                    } else {
                        //陆集陆疏交接清单校验重复
                        if("2".equals(busHandoverlistPO.getType())){
                            List<TBusHandoverlistPO>  result = tBusHandoverlistMapper.getBusHandoverlistByTrustCargoId(busHandoverlistPO.getTrustCargoId());
                            if(!result.isEmpty()){
                                throw new BusinessRuntimeException("该交接清单已填写,请返回重新进入进行修改");
                            }
                        }else{//正常的交接清单
                            List<TBusHandoverlistPO> busHandoverListByIdList = tBusHandoverlistMapper.getBusHandoverListByShipId(busHandoverlistPO.getShipvoyageItemId());
                            List<TBusCargoInfoDTO> resList = tBusHandoverlistMapper.listBusCargoInfo(busHandoverlistPO.getShipvoyageItemId(), null);
                            if ((!CollectionUtils.isEmpty(busHandoverListByIdList))) {
                                if (!(resList.size() > busHandoverListByIdList.size())) {
                                    throw new BusinessRuntimeException("该交接清单已填写,请返回重新进入进行修改");
                                }
                            }
                        }
                        busHandoverlistPO.setId(snowflake.nextId());
                        insertList.add(busHandoverlistPO);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(insertList)) {
                tBusHandoverlistMapper.insertBusHandoverlist(insertList);
            }
            if (CollectionUtils.isNotEmpty(updateList)) {
                tBusHandoverlistMapper.updateHandoverListById(updateList);
            }
        }
        // 去除末尾的逗号
        if (cargoNos.length() > 0) {
            cargoNos.deleteCharAt(cargoNos.length() - 1);
        }
        return cargoNos;
    }

    @Override
    public BigDecimal getListTon(TDisShipvoyageItemQueryDTO query) {
        BigDecimal totalTon = tBusHandoverlistMapper.getListTon(query);
        if (totalTon == null) {
            totalTon = BigDecimal.ZERO;
        }
        return totalTon;

    }

    //新增卸船交接清单
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public String addUpdateHandoverList(TBusHandoverListUnloadReqDTO dto) {
        String result= "";
        //获取船泊状态
        Integer dynamicCode = tBusHandoverlistMapper.getDynamicInfo(dto.getShipvoyageItemId(),ShipStatusEnum.WANGONG.getCode());
        if(dynamicCode>0){
            throw new BusinessRuntimeException("船已"+ShipStatusEnum.WANGONG.getName()+"，不可修改");
        }

        //文件处理
        if (!dto.getHandoverlists().isEmpty() && BusHandoverlistTypeEnum.ZHUANGXIECHUAN.getCode().equals(dto.getHandoverlists().get(0).getType())) {
            tBusHandoverlistMapper.deleteFileBusiness(dto.getHandoverlists().get(0).getShipvoyageItemId());

            if (CollectionUtils.isNotEmpty(dto.getFileIds())) {
                // 图片附件
                List<Map<String, Long>> files = dto.getFileIds().stream().map(v1 -> new HashMap<String, Long>() {{
                    put("businessId", dto.getHandoverlists().get(0).getShipvoyageItemId());
                    put("fileId", v1);
                }}).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(dto.getFileIds())) {
                    tBusHandoverlistMapper.insertFileBusiness(files);
                }
            }
            if (CollectionUtils.isNotEmpty(dto.getHgFileIds())) {
                // 海关报关单附件
                List<Map<String, Long>> hgFiles = dto.getHgFileIds().stream().map(v1 -> new HashMap<String, Long>() {{
                    put("businessId", dto.getHandoverlists().get(0).getShipvoyageItemId());
                    put("fileId", v1);
                }}).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(dto.getHgFileIds())) {
                    tBusHandoverlistMapper.insertFileBusiness(hgFiles);
                }
            }
            if (CollectionUtils.isNotEmpty(dto.getJcFileIds())) {
                // 第三方检测报告附件
                List<Map<String, Long>> jcFiles = dto.getJcFileIds().stream().map(v1 -> new HashMap<String, Long>() {{
                    put("businessId", dto.getHandoverlists().get(0).getShipvoyageItemId());
                    put("fileId", v1);
                }}).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(dto.getJcFileIds())) {
                    tBusHandoverlistMapper.insertFileBusiness(jcFiles);
                }
            }
        }
        ArrayList<TBusHandoverlistPO> updateHandoverList = new ArrayList<>();
        ArrayList<TBusHandoverlistPO> insertHandoverList = new ArrayList<>();
        ArrayList<TBusHandoverlistPO> delHandoverList = new ArrayList<>();

        //获取交接该船名航次对应的卸船交接清单
        List<TBusHandoverlistDTO> tBusHandoverlistDTOS = tBusHandoverlistMapper.listBusHandoverlist(dto.getShipvoyageItemId(), null);
        List<Long> handoverListIds = tBusHandoverlistDTOS.stream().map(TBusHandoverlistDTO::getId).collect(Collectors.toList());
        Map<Long, TBusHandoverlistDTO> dataBaseHandoverListMap = tBusHandoverlistDTOS.stream().collect(Collectors.toMap(TBusHandoverlistDTO::getId, Function.identity(), (v1, v2) -> v2));

        //新增修改删除
        if (dto.getHandoverlists().isEmpty()){
            tBusHandoverlistDTOS.stream().forEach(o->{
                TBusHandoverlistPO handoverlistPO = new TBusHandoverlistPO();
                handoverlistPO.setId(o.getId());
                handoverlistPO.setCargoInfoId(o.getCargoInfoId());
                handoverlistPO.setCargoInfoNo(o.getCargoInfoNo());
                delHandoverList.add(handoverlistPO);
            });
        }else {
            dto.getHandoverlists().forEach(o->{
                if(o.getId() == null){
                    o.setId(snowflake.nextId());
                    insertHandoverList.add(o);
                }else {
                    if(handoverListIds.contains(o.getId())){
                        updateHandoverList.add(o);
                    }
                }
            });
            List<Long> viewHandoverListIds = dto.getHandoverlists().stream().filter(o -> o.getId() != null).map(o -> o.getId()).collect(Collectors.toList());
            handoverListIds.forEach(o->{
                if(!viewHandoverListIds.contains(o)){
                    delHandoverList.add(dataBaseHandoverListMap.get(o));
                }
            });
        }
        //校验交接清单的票货是否已经下了通知单
        if (!delHandoverList.isEmpty()){
            List<Long> tmpCargoInfoId = delHandoverList.stream().map(TBusHandoverlistPO::getCargoInfoId).distinct().collect(Collectors.toList());
            tmpCargoInfoId.forEach(cargoInfoId->{
                List<TBusTrustPO> trusts = tBusHandoverlistMapper.get30TrustByCargoInfoId(cargoInfoId);
                if(!trusts.isEmpty()){
                    throw new BusinessRuntimeException("已经有发布了通知单,不允许删除");
                }
            });
        }


        //生成票货
        List<TBusCargoInfoPO> addCargoinfos = new ArrayList<>();
        List<TBusCargoInfoPO> updateCargoinfos = new ArrayList<>();

        for (TBusHandoverlistPO tmpHandoverlistPO : insertHandoverList) {
            TBusCargoInfoPO tBusCargoInfoPO = new TBusCargoInfoPO();

            tBusCargoInfoPO.setId(snowflake.nextId());
            tmpHandoverlistPO.setCargoInfoId(tBusCargoInfoPO.getId());
            tBusCargoInfoPO.setCargoInfoNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.MAIN_CARGO_INFO, null));
            tBusCargoInfoPO.setCargoCode(tmpHandoverlistPO.getCargoCode());
            tBusCargoInfoPO.setCargoName(tmpHandoverlistPO.getCargoName());
            tBusCargoInfoPO.setCargoOwnerId(tmpHandoverlistPO.getCargoOwnerId());
            tBusCargoInfoPO.setCargoOwnerName(tmpHandoverlistPO.getCargoOwnerName());
            tBusCargoInfoPO.setHatchNums(tmpHandoverlistPO.getHatchNums());
            tBusCargoInfoPO.setDeliveryId(tmpHandoverlistPO.getDeliveryId());
            tBusCargoInfoPO.setShipvoyageId(tmpHandoverlistPO.getShipvoyageId());
            tBusCargoInfoPO.setShipvoyageItemId(tmpHandoverlistPO.getShipvoyageItemId());
            tBusCargoInfoPO.setScn(tmpHandoverlistPO.getScn());
            tBusCargoInfoPO.setShipName(tmpHandoverlistPO.getShipName());
            tBusCargoInfoPO.setVoyage(tmpHandoverlistPO.getVoyage());
            tBusCargoInfoPO.setTradeType(tmpHandoverlistPO.getTradeType());
            tBusCargoInfoPO.setPackingCode(tmpHandoverlistPO.getPackingCode());
            tBusCargoInfoPO.setPackingName(tmpHandoverlistPO.getPackingName());
            tBusCargoInfoPO.setCompanyId(tmpHandoverlistPO.getCompanyId());
            tBusCargoInfoPO.setCompanyName(tmpHandoverlistPO.getCompanyName());
            tBusCargoInfoPO.setTon(tmpHandoverlistPO.getTon());
            tBusCargoInfoPO.setQuantity(new Long(tmpHandoverlistPO.getQuantity()==null?0:tmpHandoverlistPO.getQuantity().longValue()));
            tBusCargoInfoPO.setRightsQuantity(tmpHandoverlistPO.getTon());
            tBusCargoInfoPO.setSurplusRightsQuantity(tmpHandoverlistPO.getTon());
            tBusCargoInfoPO.setDeliveryNumbers(tmpHandoverlistPO.getDeliveryNumbers());
            tBusCargoInfoPO.setIsClear("0");
            tBusCargoInfoPO.setSource("10");
            tBusCargoInfoPO.setIsLogout("20");
            addCargoinfos.add(tBusCargoInfoPO);
            tBusHandoverlistMapper.addCargoInfoSingle(tBusCargoInfoPO);
        }
        //更新票货
        for (TBusHandoverlistPO updatehandoverPo : updateHandoverList) {
            TBusCargoInfoPO tBusCargoInfoPO = new TBusCargoInfoPO();
            tBusCargoInfoPO.setId(updatehandoverPo.getCargoInfoId());
            tBusCargoInfoPO.setHatchNums(updatehandoverPo.getHatchNums());
            System.out.println("nums + "+updatehandoverPo.getDeliveryNumbers());
            tBusCargoInfoPO.setDeliveryNumbers(updatehandoverPo.getDeliveryNumbers());
            tBusCargoInfoPO.setShipvoyageId(updatehandoverPo.getShipvoyageId());
            tBusCargoInfoPO.setShipvoyageItemId(updatehandoverPo.getShipvoyageItemId());
            tBusCargoInfoPO.setScn(updatehandoverPo.getScn());
            System.out.println("voyage + "+updatehandoverPo.getVoyage());
            tBusCargoInfoPO.setVoyage(updatehandoverPo.getVoyage());
            tBusCargoInfoPO.setTradeType(updatehandoverPo.getTradeType());
            tBusCargoInfoPO.setCompanyId(updatehandoverPo.getCompanyId());
            tBusCargoInfoPO.setCompanyName(updatehandoverPo.getCompanyName());
            tBusCargoInfoPO.setTon(updatehandoverPo.getTon());
            tBusCargoInfoPO.setQuantity(new Long(updatehandoverPo.getQuantity()==null?0:updatehandoverPo.getQuantity().longValue()));
            tBusCargoInfoPO.setRightsQuantity(updatehandoverPo.getTon());
            //剩余货权量的计算
            TBusCargoInfoPO tmpCargoInfoPo = tBusHandoverlistMapper.getCargoInfoById(tBusCargoInfoPO.getId());
            BigDecimal subtract = Optional.ofNullable(updatehandoverPo.getTon()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(tmpCargoInfoPo.getRightsQuantity()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(tmpCargoInfoPo.getSurplusRightsQuantity()).orElse(BigDecimal.ZERO)));
            if(subtract.compareTo(BigDecimal.ZERO)<=0){
                throw new BusinessRuntimeException(tmpCargoInfoPo.getCargoInfoNo()+"修改之后的量少于货转量");
            }
            tBusCargoInfoPO.setSurplusRightsQuantity(subtract);
            //校验货主，货名，包装
            if(tmpCargoInfoPo.getCargoOwnerId().compareTo(updatehandoverPo.getCargoOwnerId())!=0){
                throw new BusinessRuntimeException(tmpCargoInfoPo.getCargoInfoNo()+"不允许变更货主");
            }
            if(!tmpCargoInfoPo.getCargoCode().equals(updatehandoverPo.getCargoCode())){
                throw new BusinessRuntimeException(tmpCargoInfoPo.getCargoInfoNo()+"不允许变更货物");
            }
            if(!tmpCargoInfoPo.getPackingCode().equals(updatehandoverPo.getPackingCode())){
                throw new BusinessRuntimeException(tmpCargoInfoPo.getCargoInfoNo()+"不允许变更包装类型");

            }
            tBusCargoInfoPO.setCargoCode(updatehandoverPo.getCargoCode());
            tBusCargoInfoPO.setCargoName(updatehandoverPo.getCargoName());
            tBusCargoInfoPO.setCargoOwnerId(updatehandoverPo.getCargoOwnerId());
            tBusCargoInfoPO.setCargoOwnerName(updatehandoverPo.getCargoOwnerName());
            tBusCargoInfoPO.setPackingName(updatehandoverPo.getPackingName());
            tBusCargoInfoPO.setPackingCode(updatehandoverPo.getPackingCode());
            tBusCargoInfoPO.setIsClear("0");
            updateCargoinfos.add(tBusCargoInfoPO);
        }
        if(!updateCargoinfos.isEmpty()){
            List<Long> collect = updateCargoinfos.stream().filter(o->o.getId()!=null).map(TBusCargoInfoPO::getId).collect(Collectors.toList());
            if(collect.size()!=updateCargoinfos.size()){
                throw new BusinessRuntimeException("缺少票货id");
            }
            List<Long> tmpCargoInfoId = updateHandoverList.stream().map(TBusHandoverlistPO::getCargoInfoId).distinct().collect(Collectors.toList());
            tmpCargoInfoId.forEach(cargoInfoId->{
                List<TBusTrustPO> trusts = tBusHandoverlistMapper.get30TrustByCargoInfoId(cargoInfoId);
                if(!trusts.isEmpty()){
                    throw new BusinessRuntimeException("已经有票货发布了通知单,不允许修改");
                }
            });
            tBusHandoverlistMapper.updateCargoInfos(updateCargoinfos);
        }

        //生成交接清单
        if(!insertHandoverList.isEmpty()){
            insertHandoverList.forEach(tmpHandoverlistPO->{

                tmpHandoverlistPO.setStatementStatusCode("10");
                tmpHandoverlistPO.setStatementStatusName("未计算");
            });
            tBusHandoverlistMapper.insertBusHandoverlist(insertHandoverList);
        }
        //更新交接清单
        if(!updateHandoverList.isEmpty()){
            tBusHandoverlistMapper.updateHandoverListById(updateHandoverList);
            //判断对应的卸船通知单是否超量
            updateHandoverList.forEach(o->{
                List<TBusTrustPO> trustPOS = tBusHandoverlistMapper.getTrustByCargoInfoId(o.getCargoInfoId());
                if (!trustPOS.isEmpty()) {
                    trustPOS = trustPOS.stream().filter(v1 -> "卸船".equals(v1.getType())).collect(Collectors.toList());
                    //执行之后只会有一个卸船通知单
                    if(!trustPOS.isEmpty()){
                        //判断超量
                        trustPOS.forEach(v1->{
                            //获取通知单关联的交接清单
                            List<TBusHandoverListDTO> tBusHandoverlist = tBusHandoverlistMapper.getHandoverListByTrustId(v1.getId());
                            //算交接清单的浮动上下限
                            BigDecimal minTon = tBusHandoverlist.stream().map(v2->{
                                return   v2.getTon().subtract(Optional.ofNullable(v2.getTon()).orElse(BigDecimal.ZERO).multiply(Optional.of(v2.getFloatTon().divide(new BigDecimal(1000))).orElse(BigDecimal.ZERO)));
                            }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                            BigDecimal maxTon = tBusHandoverlist.stream().map(v2->{
                                return   v2.getTon().add(Optional.ofNullable(v2.getTon()).orElse(BigDecimal.ZERO).multiply(Optional.of(v2.getFloatTon().divide(new BigDecimal(1000))).orElse(BigDecimal.ZERO)));
                            }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                            //通知单有没有超量 需要判断通知单的量是否超交接清单的比最大浮动量
                            if(v1.getPlanTon().compareTo(maxTon)>0){
                               throw new BusinessRuntimeException("通知单量大于最大浮动量"+maxTon+"涉及的票货:"+"<br>"+tBusHandoverlist.stream().map(tmp->{return tmp.getCargoInfoNo()+"浮动量"+tmp.getFloatTon().divide(new BigDecimal("1000"));}).distinct().collect(Collectors.joining(",")));
                           }
                        });
                    }
                }
            });
        }
        //删除交接清单 删除票货
        if(!delHandoverList.isEmpty()){
            //校验是否下过通知单
            delHandoverList.forEach(o->{
                List<TBusTrustPO> tmpTrust = tBusHandoverlistMapper.getTrustByCargoInfoId(o.getCargoInfoId());
                if(!tmpTrust.isEmpty()){
                    throw new BusinessRuntimeException(o.getCargoInfoNo()+"已经下过通知单，不能删除。");
                }
            });
            tBusHandoverlistMapper.deleteCargoInfoByIds(delHandoverList.stream().map(TBusHandoverlistPO::getCargoInfoId).collect(Collectors.toList()));
            tBusHandoverlistMapper.deleteBusHandoverlistByIds(delHandoverList.stream().map(TBusHandoverlistPO::getId).collect(Collectors.toList()));
        }
        return result;
    }

    @Override
    public boolean isHaveTrust(Long cargoInfoId) {
        return !tBusHandoverlistMapper.getTrustByCargoInfoId(cargoInfoId).isEmpty();
    }

    /**
     * 校验当前航次有无交接清单，是否生成了通知单
     * @param voyageId
     * @return
     */
    @Override
    public boolean checkHaveTrust(Long voyageId) {
        return !tBusHandoverlistMapper.checkHaveTrust(voyageId).isEmpty();
    }
}
