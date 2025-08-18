package com.yy.ppm.produce.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.produce.bean.dto.*;
import com.yy.ppm.produce.mapper.TPrdPlanEntrustMapper;
import com.yy.ppm.produce.service.TPrdPlanEntrustService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TPrdPlanEntrustServiceImpl implements TPrdPlanEntrustService {

    @Resource
    private TPrdPlanEntrustMapper tPrdPlanEntrustMapper;

    private static final String IS_FINISHED_NO = "0"; // 过磅是否完成（是否出港，0 在港；1不在港）
    private static final String PACKING_SAN = "01"; // 散货
    private static final String IN_PORT = "0"; // 在港


    @Override
    public Pages<TPrdPlanEntrustResultDTO> getPage(TPrdPlanEntrustSearchDTO searchDTO) {
        /*UserInfo userInfo = securityUtils.getUserInfo();
        if (!BusinessConstants.IS_ADMIN.equals(userInfo.getIsSuperadmin())) {
            if (userInfo.getCustomerId() != null) {
                searchDTO.setCargoOwnerId(userInfo.getCustomerId());
            } else {
                Pages pages = new Pages<>();
                pages.setPages(new ArrayList());
                return pages;
            }
        }*/
        Pages<TPrdPlanEntrustResultDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tPrdPlanEntrustMapper.getPage(searchDTO);
        });
        if (CollectionUtils.isNotEmpty(pages.getPages())) {
            Map<String, TPrdPlanEntrustVehicleDTO> vehicleMap = new HashMap<>();
            // 查询已预约车辆
            List<String> planNoList
                    = pages.getPages().stream().map(TPrdPlanEntrustResultDTO::getPlanNo).collect(Collectors.toList());
            List<TPrdPlanEntrustVehicleDTO> vehicleList = tPrdPlanEntrustMapper.getVehicleNumByPlanNo(planNoList);
            if (CollectionUtils.isNotEmpty(vehicleList)) {
                vehicleMap = vehicleList.stream().collect(Collectors.toMap(item -> item.getPlanNo(), item -> item));
            }
            // 查询车辆过磅记录
            List<TPrdVehiclePoundDTO> vehiclePoundList = tPrdPlanEntrustMapper.getVehiclePound(planNoList);
            for (TPrdPlanEntrustResultDTO e : pages.getPages()) {
                e.setSurplusTon(e.getPlanTon().subtract(e.getFinishedTon()));
                TPrdPlanEntrustCargoDTO cargo = new TPrdPlanEntrustCargoDTO();
                cargo.setQuantity(e.getQuantity());
                cargo.setTon(e.getPlanTon());
                if(PACKING_SAN.equals(e.getPackingCode())){
                    cargo.setQuantity(0);
                }
                e.setCargoList(Arrays.asList(cargo));
                if (vehicleMap.containsKey(e.getPlanNo())) {
                    TPrdPlanEntrustVehicleDTO tmpDto = vehicleMap.get(e.getPlanNo());
                    e.setOrderNum(tmpDto.getOrderNum());
                    e.setTruckStopNum(tmpDto.getStopNum());
                }
                int inPortNum = 0;
                if (CollectionUtils.isNotEmpty(vehiclePoundList)) {
                    for (TPrdVehiclePoundDTO poundDTO : vehiclePoundList) {
                        if (e.getPlanNo().equals(poundDTO.getPlanNo())) {
                            if (IS_FINISHED_NO.equals(poundDTO.getIsFinished())) {
                                inPortNum++;
                            }
                        }
                    }
                }
                e.setTruckInPortNum(inPortNum);
                if (e.getFinishedTon().compareTo(BigDecimal.ZERO) > 0) {
                    e.setIsWeigh("1");
                }
            }
        }
        return pages;
    }

    @Override
    public Pages<TPrdVehicleReservationDTO> getVehicleList(TPrdVehicleReservationSearchDTO searchDTO) {
        Pages<TPrdVehicleReservationDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tPrdPlanEntrustMapper.getVehicleList(searchDTO);
        });
        pages.getPages().stream().forEach(item -> {
            item.setTrendStatusLabel(IN_PORT.equals(item.getTrendStatus()) ? "在港" : "不在港");
        });
        return pages;
    }
}
