package com.yy.ppm.tallyExtrinsic.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.HttpUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.tallyExtrinsic.bean.dto.DbCargoInfoDTO;
import com.yy.ppm.tallyExtrinsic.bean.dto.DbTruckDTO;
import com.yy.ppm.tallyExtrinsic.bean.dto.TBusReservationConfirmDTO;
import com.yy.ppm.tallyExtrinsic.bean.dto.TBusReservationConfirmSearchDTO;
import com.yy.ppm.tallyExtrinsic.mapper.TBusReservationConfirmMapper;
import com.yy.ppm.tallyExtrinsic.service.TBusReservationConfirmService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 前沿确认
 *
 * @author wangxd
 * @date 2021-03-18 10:52:04
 */
@Service
public class TBusReservationConfirmServiceImpl implements TBusReservationConfirmService {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(TBusReservationConfirmServiceImpl.class);

    @Value("${dblh.url}")
    private String dblhUrl;

    @Resource
    private TBusReservationConfirmMapper tBusReservationConfirmMapper;


    private static final String STATUS_ARRIVAL = "1"; // 确认到达
    private static final String STATUS_LEAVE = "2"; // 确认离开

    private static final String CHECK_TALLY_QUANTITY = "/bagTally/tosApi/receiveTruckEnd"; // 吨包理货校验接口

    @Override
    public TBusReservationConfirmDTO getConfirm(TBusReservationConfirmSearchDTO searchDTO) {
        if (StringUtils.isBlank(searchDTO.getStatus())) {
            throw new BusinessRuntimeException("确认状态不能为空~");
        }
        if (STATUS_ARRIVAL.equals(searchDTO.getStatus())) {
            if (StringUtils.isBlank(searchDTO.getMacNo())) {
                throw new BusinessRuntimeException("门机编号不能为空~");
            }
            List<TBusReservationConfirmDTO> confirmList = tBusReservationConfirmMapper.getConfirm(searchDTO);
            if (CollectionUtils.isEmpty(confirmList)) {
                throw new BusinessRuntimeException("未查询到司机确认到达记录~");
            }
            if (confirmList.size() > 1) {
                throw new BusinessRuntimeException("门机【" +searchDTO.getMacNo()+ "】查询到多个司机确认到达记录~");
            }
            return confirmList.get(0);
        } else if(STATUS_LEAVE.equals(searchDTO.getStatus())) {
            if (searchDTO.getId() == null) {
                throw new BusinessRuntimeException("前沿确认记录主键不能为空~");
            }
            tBusReservationConfirmMapper.confirmEnd(searchDTO.getId());
            TBusReservationConfirmDTO entity = tBusReservationConfirmMapper.getById(searchDTO);
            if (entity == null) {
                throw new BusinessRuntimeException("数据异常，未查询到确认离开记录~");
            }
            return entity;
        }
        return null;
    }

    @Override
    public int deleteConfirm(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("前沿确认记录主键不能为空~");
        }
        TBusReservationConfirmSearchDTO searchDTO = new TBusReservationConfirmSearchDTO();
        searchDTO.setId(id);
        TBusReservationConfirmDTO entity = tBusReservationConfirmMapper.getById(searchDTO);
        if (entity == null) {
            throw new BusinessRuntimeException("数据异常，未查询到确认到达记录~");
        }
        if (STATUS_LEAVE.equals(entity.getStatus())) {
            throw new BusinessRuntimeException("数据异常，作业已完成无法删除~");
        }
        return tBusReservationConfirmMapper.deleteConfirm(id);
    }

    @Override
    public List<TBusReservationConfirmDTO> getConfirmList(TBusReservationConfirmSearchDTO searchDTO) {
        List<TBusReservationConfirmDTO> confirmList = tBusReservationConfirmMapper.getConfirmList(searchDTO);
        return confirmList;
    }

    @Override
    public boolean checkTallyQuantity(JSONObject params) {
        boolean flag = true;
        try {
            String result = HttpUtils.sendGetRetry(dblhUrl + CHECK_TALLY_QUANTITY,
                    "id=" + params.getString("id") + "&quantity=" + params.getString("quantity"),
                    "UTF-8");
            if (StringUtils.isNotBlank(result)) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (200 == jsonObject.getIntValue("code")
                        && jsonObject.getBooleanValue("success")) {
                    flag = true;
                } else {
                    flag = false;
                }
            } else {
                flag = false;
            }
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }


    /**
     * 根据航次ID查询指令票货
     * @param shipVoyageItemId
     * @return
     */
    @Override
    public List<DbCargoInfoDTO> getPhByShip(String shipVoyageItemId) {
        return tBusReservationConfirmMapper.getPhByShip(shipVoyageItemId);
    }

    @Override
    public List<DbTruckDTO> getTruckList(String cargoInfoId, String truckNo, String shipVoyageItemId) {
        return tBusReservationConfirmMapper.getTruckList(cargoInfoId, truckNo, shipVoyageItemId);
    }
}
