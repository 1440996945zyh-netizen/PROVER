package com.yy.ppm.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.Page;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.UserHelper;
import com.google.common.collect.*;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.business.controller.TBusDispatchReleaseController;

import com.yy.ppm.business.mapper.TBusDispatchReleaseDetailMapper;
import com.yy.ppm.business.service.TBusDispatchReleaseService;
import com.yy.ppm.business.mapper.TBusDispatchReleaseMapper;
import com.yy.ppm.common.enums.BusTrustStatusEnum;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageQueryDTO;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.apache.commons.collections.CollectionUtils;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 放行单表(TBusDispatchRelease)ServiceImpl
 * @Description
 * @createTime 2024年04月16日 17:07:00
 */
@Service
public class TBusDispatchReleaseServiceImpl implements TBusDispatchReleaseService {

    @Resource
    private TBusDispatchReleaseMapper tBusDispatchReleaseMapper;
    @Resource
    private TBusDispatchReleaseDetailMapper tBusDispatchReleaseDetailMapper;

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TBusDispatchReleaseController.class);

    @Resource
    private Snowflake snowflake;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<Map<String, Object>> getPageList(TBusDispatchReleaseSearchDTO searchDTO) {
        final String methodName = "TBusDispatchReleaseServiceImpl:getPageList";
        try {
            LOGGER.info(methodName, "获取列表（翻页）");
            //按照创建时间倒叙排列
            Pages<Map<String, Object>> pages = PageHelperUtils.limit(searchDTO, () -> {
                Page<Map<String, Object>> page = tBusDispatchReleaseMapper.getPageList(searchDTO);
                return page;
            });
            return pages;
        } catch (Exception e) {
            LOGGER.error(methodName, e.getMessage());
            return new Pages<>();
        }
    }

    /**
     * 获取列表
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public List<TBusDispatchReleaseDTO> getListByCondition(TBusDispatchReleaseSearchDTO searchDTO) {
        final String methodName = "TBusDispatchReleaseServiceImpl:getListByCondition";
        try {
            LOGGER.info(methodName, "获取列表");
            List<TBusDispatchReleaseDTO> list = tBusDispatchReleaseMapper.exportList(searchDTO);
            //按照创建时间倒叙排列
            list.sort(((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())));
            return list;
        } catch (Exception e) {
            LOGGER.error(methodName, e.getMessage());
            return Lists.newArrayList();
        }
    }

    @Override
    public List<TBusDispatchReleaseDetailDTO> cargoInfoListByCondition(TBusDispatchReleaseDetailSearchDTO searchDTO) {
        final String methodName = "TBusDispatchReleaseServiceImpl:cargoInfoListByCondition";
        try {
            LOGGER.info(methodName, "获取放行单下票货列表");
            List<TBusDispatchReleaseDetailDTO> list = tBusDispatchReleaseDetailMapper.exportList(searchDTO);
            //按照创建时间倒叙排列
            list.sort(((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())));
            return list;
        } catch (Exception e) {
            LOGGER.error(methodName, e.getMessage());
            return Lists.newArrayList();
        }
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    @Override
    public TBusDispatchReleaseDTO getDetail(Long id) {
        return tBusDispatchReleaseMapper.getById(id);
    }


    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TBusDispatchReleaseDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return tBusDispatchReleaseMapper.insert(dto) == 1;
            // 修改
        } else {
            return tBusDispatchReleaseMapper.update(dto) == 1;
        }

    }


    /**
     * 批量保存
     * @param tBusDispatchReleaseDTOS
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> doListSave(List<TBusDispatchReleaseDTO> tBusDispatchReleaseDTOS) {
        final String methodName = "TBusDispatchReleaseServiceImpl:doListSave";
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            LOGGER.info(methodName, "批量保存");
            if (CollectionUtils.isEmpty(tBusDispatchReleaseDTOS)) {
                resultMap.put("flag", false);
                resultMap.put("msg", "不能保存空数据");
                return resultMap;
            }
            Map<Integer, List<TBusDispatchReleaseDTO>> statusMap = tBusDispatchReleaseDTOS
                    .stream().collect(Collectors.groupingBy(TBusDispatchReleaseDTO::getStatus));
            List<TBusDispatchReleaseDTO> deleteList = statusMap.get(0);//删除
            List<TBusDispatchReleaseDTO> saveList = statusMap.get(1);//保存
            List<TBusDispatchReleaseDTO> updateList = statusMap.get(2);//更新
            if (CollectionUtils.isNotEmpty(saveList)) {//批量保存
                if (CollectionUtils.isEmpty(deleteList) && CollectionUtils.isEmpty(updateList)) {
                    TBusDispatchReleaseSearchDTO searchDTO = new TBusDispatchReleaseSearchDTO();
                    BeanUtil.copyProperties(saveList.get(0), searchDTO);
                    List<TBusDispatchReleaseDTO> list = getListByCondition(searchDTO);
                    if (CollectionUtils.isNotEmpty(list)) {
                        resultMap.put("flag", false);
                        resultMap.put("msg", "不能保存重复数据");
                        return resultMap;
                    }
                }
                saveList.forEach(e -> e.setId(snowflake.nextId()));
                tBusDispatchReleaseMapper.insertList(saveList);
                detailListSave(saveList);
            }
            //批量删除
            if (CollectionUtils.isNotEmpty(deleteList)) {
                List<Long> ids = deleteList.stream().map(TBusDispatchReleaseDTO::getId).collect(Collectors.toList());
                deleteListByIds(ids);
                deleteDetailDispatchReleaseIds(ids);
            }
            //批量更新
            if (CollectionUtils.isNotEmpty(updateList)) {
                List<Long> ids = updateList.stream().map(TBusDispatchReleaseDTO::getId).collect(Collectors.toList());
                tBusDispatchReleaseMapper.updateListById(updateList);
                deleteDetailDispatchReleaseIds(ids);
                detailListSave(updateList);
            }
            resultMap.put("flag", true);
            resultMap.put("msg", "保存成功");
            return resultMap;
        } catch (Exception e) {
            LOGGER.error(methodName, e.getMessage());
            resultMap.put("flag", false);
            resultMap.put("msg", e.getMessage());
            return resultMap;
        }
    }

    private void updateTrustCargo(List<TBusDispatchReleaseDTO> list,String type){
        if("del".equals(type)){

        }else{

        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCargoList(List<TBusTrustCargoDTO> trustCargoDTOS) {
        if(CollectionUtils.isEmpty(trustCargoDTOS)){
            throw new BusinessRuntimeException("无票货信息可生成提运单");
        }
        List<TBusTrustCargoDTO> list = Lists.newArrayList();
        for (TBusTrustCargoDTO trustCargoDTO : trustCargoDTOS) {
            if(StringUtil.isNotEmpty(trustCargoDTO.getDeliveryNumbers())){
                list.add(trustCargoDTO);
            }
        }
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        TBusTrustCargoDTO trustCargoDTO = list.get(0);
        Long shipvoyageItemId = trustCargoDTO.getShipvoyageItemId()!=null?Long.valueOf(trustCargoDTO.getShipvoyageItemId()):null;
        Map<String, List<TBusTrustCargoDTO>> statusMap = list
                .stream().collect(Collectors.groupingBy(TBusTrustCargoDTO::getDeliveryNumbers));
        List<TBusDispatchReleaseDTO> dispatchReleaseDTOS = Lists.newArrayList();
        List<TBusDispatchReleaseDetailDTO> dispatchReleaseDetailDTOS = Lists.newArrayList();
        statusMap.forEach((k,v)->{
            if(!StringUtils.isEmpty(k)){
                //开始创建提运单
                TBusDispatchReleaseDTO dispatchReleaseDTO = new TBusDispatchReleaseDTO();
                dispatchReleaseDTO.setId(snowflake.nextId());
                dispatchReleaseDTO.setShipvoyageItemId(shipvoyageItemId);
                dispatchReleaseDTO.setDeliveryNumbers(k);
//                dispatchReleaseDTO.setPermitThrough("0");//不放行
                dispatchReleaseDTOS.add(dispatchReleaseDTO);
                for (TBusTrustCargoDTO item : v) {
                    if(StringUtil.isNotEmpty(item.getCargoInfoNo())){
                        TBusDispatchReleaseDetailDTO detail = new TBusDispatchReleaseDetailDTO();
                        detail.setId(snowflake.nextId());
                        detail.setDispatchReleaseId(dispatchReleaseDTO.getId());
                        detail.setDeliveryNumbers(k);
                        detail.setCargoInfoNo(item.getCargoInfoNo());
                        detail.setCargoOwnerId(String.valueOf(item.getCargoOwnerId()));//货主
                        detail.setCargoOwnerName(item.getCargoOwnerName());//货主
                        detail.setCargoCode(item.getCargoCode());
                        detail.setCargoName(item.getCargoName());
                        detail.setQuantity(item.getQuantity());
                        detail.setTon(item.getTon());
                        detail.setPackingCode(item.getPackingCode());
                        detail.setPackingName(item.getPackingName());
                        dispatchReleaseDetailDTOS.add(detail);
                    }
                }
            }
        });
        if(CollectionUtils.isNotEmpty(dispatchReleaseDTOS)){
            tBusDispatchReleaseMapper.insertList(dispatchReleaseDTOS);
        }
        if(CollectionUtils.isNotEmpty(dispatchReleaseDetailDTOS)){
            tBusDispatchReleaseMapper.insertDetailList(dispatchReleaseDetailDTOS);
         }
    }

    @Override
    public void deleteDispatchRelease(List<TBusTrustCargoDTO> trustCargoDTOS) {
        if(CollectionUtils.isEmpty(trustCargoDTOS)){
            return;
        }
        List<TBusTrustCargoDTO> list = Lists.newArrayList();
        for (TBusTrustCargoDTO trustCargoDTO : trustCargoDTOS) {
            if(StringUtil.isNotEmpty(trustCargoDTO.getDeliveryNumbers())){
                list.add(trustCargoDTO);
            }
        }
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        Long shipvoyageItemId = list.get(0).getShipvoyageItemId()!=null?Long.valueOf(list.get(0).getShipvoyageItemId()):null;
        String deliveryNumbers = list.get(0).getDeliveryNumbers();
        TBusDispatchReleaseSearchDTO releaseSearchDT = new TBusDispatchReleaseSearchDTO();
        releaseSearchDT.setShipvoyageItemId(shipvoyageItemId);
        List<String> deliveryNumberList = Lists.newArrayList();
        for (TBusTrustCargoDTO trustCargoDTO : list) {
            deliveryNumberList.addAll(trustCargoDTO.getDeliveryList());
        }
        releaseSearchDT.setDeliveryList(deliveryNumberList);

        List<TBusDispatchReleaseDTO> dispatchReleaseDTOS = tBusDispatchReleaseMapper.exportList(releaseSearchDT);
        List<Long> dispatchReleaseIds = dispatchReleaseDTOS.stream().map(TBusDispatchReleaseDTO::getId).collect(Collectors.toList());
        List<String> cargoInfoNos = list.stream().map(TBusTrustCargoDTO::getCargoInfoNo).collect(Collectors.toList());
        //删除放行单子表
        tBusDispatchReleaseMapper.deleteDetailByCondition(CollectionUtils.isEmpty(dispatchReleaseIds)?null:dispatchReleaseIds
                ,CollectionUtils.isEmpty(cargoInfoNos)?null:cargoInfoNos);
        //删除放行单
        for (Long dispatchReleaseId : dispatchReleaseIds) {
            tBusDispatchReleaseMapper.deleteCargoInfoIsNullById(dispatchReleaseId);
        }
    }

    /**
     * 保存
     * @param list
     */
    private void detailListSave(List<TBusDispatchReleaseDTO> list) {
        List<TBusDispatchReleaseDetailDTO> tBusDispatchReleaseDetailDTOS = Lists.newArrayList();
        for (TBusDispatchReleaseDTO dto : list) {
            Long dispatchReleaseId = dto.getId();
            String deliveryNumbers = dto.getDeliveryNumbers();
            List<String> cargoInfoNoList = dto.getCargoInfoNoList();
            List<TBusCargoInfoDTO> cargoInfoByNos = tBusDispatchReleaseMapper.getCargoInfoByNos(cargoInfoNoList);
            if(CollectionUtils.isEmpty(cargoInfoByNos)){
                throw new BusinessRuntimeException("没有对应票货号");
            }
            Map<String, List<TBusCargoInfoDTO>> cargoInfoMap = cargoInfoByNos
                    .stream().collect(Collectors.groupingBy(TBusCargoInfoDTO::getCargoInfoNo));
            for (String cargoInfoNo : cargoInfoNoList) {
                cargoInfoNo = cargoInfoNo.trim();
                TBusCargoInfoDTO cargoInfoDTO = ObjectUtils.isEmpty(cargoInfoMap.get(cargoInfoNo))?new TBusCargoInfoDTO():cargoInfoMap.get(cargoInfoNo).get(0);
                TBusDispatchReleaseDetailDTO detail = new TBusDispatchReleaseDetailDTO();
                detail.setId(snowflake.nextId());
                detail.setDispatchReleaseId(dispatchReleaseId);
                detail.setDeliveryNumbers(deliveryNumbers);
                detail.setCargoInfoNo(cargoInfoNo);
                detail.setCargoOwnerId(String.valueOf(cargoInfoDTO.getCargoOwnerId()));//货主
                detail.setCargoOwnerName(cargoInfoDTO.getCargoOwnerName());//货主
                detail.setCargoCode(cargoInfoDTO.getCargoCode());
                detail.setCargoName(cargoInfoDTO.getCargoName());
                detail.setQuantity(cargoInfoDTO.getQuantity());
                detail.setTon(cargoInfoDTO.getTon());
                detail.setPackingCode(cargoInfoDTO.getPackingCode());
                detail.setPackingName(cargoInfoDTO.getPackingName());
                detail.setRemark(dto.getRemark());
                tBusDispatchReleaseDetailDTOS.add(detail);
            }

        }
        tBusDispatchReleaseMapper.insertDetailList(tBusDispatchReleaseDetailDTOS);
    }






    /**
     * 删除子表
     * @param
     */
    private void deleteDetailDispatchReleaseIds(List<Long> ids){
        tBusDispatchReleaseMapper.deleteDetailDispatchReleaseIds(ids);
    }

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return tBusDispatchReleaseMapper.deleteById(id) == 1;
    }

    /**
     * 批量删除
     * List<Long> ids
     *
     * @param ids
     * @return 是否成功
     */
    @Override
    public boolean deleteListByIds(List<Long> ids) {
        //判断一下通知单是否已经发布，如果发布则不允许删除
        List<TBusDispatchReleaseDTO> list = tBusDispatchReleaseMapper.getByIds(ids);
        for (TBusDispatchReleaseDTO dispatchReleaseDTO : list) {
            TBusTrustDTO trustDTO = tBusDispatchReleaseMapper.getBusTrustDTO(dispatchReleaseDTO.getShipvoyageItemId(),dispatchReleaseDTO.getDeliveryNumbers());
            if (StringUtil.getInt(trustDTO.getStatus()) >= Integer.parseInt(BusTrustStatusEnum.YFB.getCode())) {
                throw new BusinessRuntimeException(trustDTO.getDeliveryNumbers()+"  已发布,请先撤销通知单发布");
            }
        }
        return tBusDispatchReleaseMapper.deleteListByIds(ids) >= 1;
    }

    /**
     * 批量删除
     *
     * @param tBusDispatchReleaseDTO
     * @return 是否成功
     */
    @Override
    public boolean deleteByCondition(TBusDispatchReleaseDTO tBusDispatchReleaseDTO) {
        return tBusDispatchReleaseMapper.deleteByCondition(tBusDispatchReleaseDTO) >= 1;
    }

}

