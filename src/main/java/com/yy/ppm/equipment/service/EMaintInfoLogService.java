package com.yy.ppm.equipment.service;

import com.yy.ppm.equipment.bean.dto.EMaintInfoLogDTO;
import com.yy.ppm.equipment.enums.EMaintInfoLogActionEnum;

import java.util.List;

/**
 * 设备维修操作日志 Service
 */
public interface EMaintInfoLogService {

    /**
     * 写入操作日志
     */
    void saveLog(Long maintInfoId, String workOrderNo, EMaintInfoLogActionEnum action,
                 Integer fromStatus, Integer toStatus, String remark, String snapshotJson);

    /**
     * 查询维修工单操作日志
     */
    List<EMaintInfoLogDTO> getListByMaintInfoId(Long maintInfoId);
}
