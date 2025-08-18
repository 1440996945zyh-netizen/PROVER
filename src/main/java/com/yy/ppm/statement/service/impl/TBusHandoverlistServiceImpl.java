package com.yy.ppm.statement.service.impl;


import cn.hutool.core.lang.Snowflake;

import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.enums.BusHandoverlistTypeEnum;
import com.yy.ppm.common.enums.HandoverlistStatusEnum;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.statement.bean.dto.busHandoverlist.*;
import com.yy.ppm.statement.bean.dto.storageSettle.VWeightInfo;
import com.yy.ppm.statement.bean.po.TBusHandoverlistPO;
import com.yy.ppm.statement.mapper.TBusHandoverlistMapper;
import com.yy.ppm.statement.service.TBusHandoverlistService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-07 10:58
 */
@Service
public class TBusHandoverlistServiceImpl implements TBusHandoverlistService {

    @Autowired
    private TBusHandoverlistMapper tBusHandoverlistMapper;

    @Autowired
    private Snowflake snowflake;

    @Resource
    private SysFileService sysFileService;

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

            if(trustId!=null){
                //集港过磅量
                VWeightInfo VJInfo= tBusHandoverlistMapper.getJGWeightTonByCargoInfo(pos.getId());
                pos.setJGweightGoods(VJInfo==null?null:VJInfo.getWeightGoods());
                //疏港过磅量
                VWeightInfo VSInfo= tBusHandoverlistMapper.getSGWeightTonByCargoInfo(pos.getId());
                pos.setSGweightGoods(VSInfo==null?null:VSInfo.getWeightGoods());
            }
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
                                        !busHandoverListById.getRemark().equals(busHandoverlistPO.getRemark()))||
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
}
