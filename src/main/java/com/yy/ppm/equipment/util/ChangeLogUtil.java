package com.yy.ppm.equipment.util;

import cn.hutool.core.lang.Snowflake;
import com.yy.ppm.equipment.bean.po.MEquipmentChangeLogDetailPO;
import com.yy.ppm.equipment.bean.po.MEquipmentChangeLogPO;
import com.yy.ppm.equipment.mapper.MEquipmentChangeLogDetailMapper;
import com.yy.ppm.equipment.mapper.MEquipmentChangeLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 变更记录工具类
 * @author system
 */
@Component
public class ChangeLogUtil {

    @Resource
    private MEquipmentChangeLogMapper changeLogMapper;

    @Resource
    private MEquipmentChangeLogDetailMapper changeLogDetailMapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 记录变更
     * @param equipId 设备ID
     * @param changeType 变更类型：BASIC_INFO、FINANCE、SUPPLY、SPECIAL_INFO
     * @param changes 变更字段Map，key为字段名，value为[旧值, 新值, 字段中文名]的数组
     */
    public void recordChange(Long equipId, String changeType, Map<String, Object[]> changes) {
        if (changes == null || changes.isEmpty()) {
            return;
        }

        // 创建变更记录主表
        MEquipmentChangeLogPO logPO = new MEquipmentChangeLogPO();
        logPO.setId(snowflake.nextId());
        logPO.setEquipId(equipId);
        logPO.setChangeType(changeType);
        logPO.setChangeTime(new Date());
        changeLogMapper.insert(logPO);

        // 创建变更记录子表
        List<MEquipmentChangeLogDetailPO> detailList = new ArrayList<>();
        for (Map.Entry<String, Object[]> entry : changes.entrySet()) {
            String field = entry.getKey();
            Object[] values = entry.getValue();
            if (values == null || values.length < 2) {
                continue;
            }

            String oldValue = values[0] != null ? String.valueOf(values[0]) : null;
            String newValue = values[1] != null ? String.valueOf(values[1]) : null;
            String fieldName = values.length > 2 && values[2] != null ? String.valueOf(values[2]) : field;

            // 如果值没有变化，跳过
            if (equalsValue(oldValue, newValue)) {
                continue;
            }

            MEquipmentChangeLogDetailPO detailPO = new MEquipmentChangeLogDetailPO();
            detailPO.setId(snowflake.nextId());
            detailPO.setChangeLogId(logPO.getId());
            detailPO.setChangeField(field);
            detailPO.setChangeFieldName(fieldName);
            detailPO.setOldValue(oldValue);
            detailPO.setNewValue(newValue);
            detailList.add(detailPO);
        }

        if (!detailList.isEmpty()) {
            changeLogDetailMapper.insertBatch(detailList);
        }
    }

    /**
     * 比较两个值是否相等
     */
    private boolean equalsValue(String oldValue, String newValue) {
        if (oldValue == null && newValue == null) {
            return true;
        }
        if (oldValue == null || newValue == null) {
            return false;
        }
        return oldValue.equals(newValue);
    }
}

