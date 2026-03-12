package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.util.SecurityUtils;
import com.yy.ppm.equipment.bean.dto.EMaintInfoLogDTO;
import com.yy.ppm.equipment.bean.po.EMaintInfoLogPO;
import com.yy.ppm.equipment.enums.EMaintInfoLogActionEnum;
import com.yy.ppm.equipment.mapper.EMaintInfoLogMapper;
import com.yy.ppm.equipment.service.EMaintInfoLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 设备维修操作日志 Service 实现
 */
@Service
public class EMaintInfoLogServiceImpl implements EMaintInfoLogService {

    @Resource
    private EMaintInfoLogMapper maintInfoLogMapper;

    @Resource
    private Snowflake snowflake;

    @Resource
    private SecurityUtils securityUtils;

    @Override
    public void saveLog(Long maintInfoId, String workOrderNo, EMaintInfoLogActionEnum action,
                        Integer fromStatus, Integer toStatus, String remark, String snapshotJson) {
        Date now = new Date();
        EMaintInfoLogPO po = new EMaintInfoLogPO();
        po.setId(snowflake.nextId());
        po.setMaintInfoId(maintInfoId);
        po.setWorkOrderNo(workOrderNo);
        po.setActionCode(action.getCode());
        po.setActionName(action.getName());
        po.setFromStatus(fromStatus);
        po.setToStatus(toStatus);
        po.setOperateBy(securityUtils.getLoginUserId());
        po.setOperateByName(securityUtils.getLoginUserName());
        po.setOperateTime(now);
        po.setRemark(remark);
        po.setSnapshotJson(snapshotJson);
        po.setDelFlag(0);
        po.setNow(now);
        po.setLoginUserId(securityUtils.getLoginUserId());
        po.setLoginUserName(securityUtils.getLoginUserName());
        maintInfoLogMapper.insert(po);
    }

    @Override
    public List<EMaintInfoLogDTO> getListByMaintInfoId(Long maintInfoId) {
        return maintInfoLogMapper.selectListByMaintInfoId(maintInfoId);
    }
}
