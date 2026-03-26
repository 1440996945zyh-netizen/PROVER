package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.equipment.bean.dto.EMaterialWarningConfigDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarningConfigSearchDTO;
import com.yy.ppm.equipment.bean.po.EMaterialWarningConfigPO;
import com.yy.ppm.equipment.bean.po.EMaterialWarningRecordPO;
import com.yy.ppm.equipment.mapper.EMaterialWarehouseInDetailMapper;
import com.yy.ppm.equipment.mapper.EMaterialWarningConfigMapper;
import com.yy.ppm.equipment.mapper.EMaterialWarningRecordMapper;
import com.yy.ppm.equipment.service.EMaterialWarningConfigService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author FanQi
 * @version 1.0
 * @data 2026/3/20 10:50
 * @Description 物资预警配置
 */
@Service
public class EMaterialWarningConfigServiceImpl implements EMaterialWarningConfigService {

   @Autowired
    private EMaterialWarningConfigMapper eMaterialWarningConfigMapper;

    @Autowired
    private EMaterialWarehouseInDetailMapper eMaterialWarehouseInDetailMapper;

    @Autowired
    private EMaterialWarningRecordMapper eMaterialWarningRecordMapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 主列表查询物资预警配置
     * @param searchDTO
     * @return
     */
    @Override
    public Pages<EMaterialWarningConfigDTO> getList(EMaterialWarningConfigSearchDTO searchDTO) {
        Pages<EMaterialWarningConfigDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return eMaterialWarningConfigMapper.selectList(searchDTO);
        });
        return pages;
    }

    /**
     * 根据ID查询物资预警配置
     */
    @Override
    public EMaterialWarningConfigDTO getById(Long id) {
        return eMaterialWarningConfigMapper.selectById(id);
    }

    /**
     * 新增/修改资预警配置
     * @param po
     */
    @Override
    public void save(EMaterialWarningConfigPO po) {
        if(null == po.getId()){
            po.setId(snowflake.nextId());
            eMaterialWarningConfigMapper.add(po);
        }else{
            eMaterialWarningConfigMapper.update(po);
        }
    }

    /**
     * 修改状态
     */
    @Override
    public void updateStatus(EMaterialWarningConfigPO po) {
        eMaterialWarningConfigMapper.updateStatus(po);
    }

    /**
     * 删除资预警配置
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        eMaterialWarningConfigMapper.delete(id);
    }

//    /**
//     * 服务启动后先跑一次
//     */
//    @EventListener(ApplicationReadyEvent.class)
//    public void runAfterStartup() {
//        LOGGER.enter("物资预警启动触发开始");
//        Integer count = generateWarningRecord();
//        LOGGER.exit("物资预警启动触发结束, count:" + count);
//    }


    /**
     * 扫描预警配置，生成预警消息
     */
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public Integer generateWarningRecord() {
        // 只扫启用中的预警配置
        List<EMaterialWarningConfigDTO> configList = eMaterialWarningConfigMapper.selectEnabledList();
        if (configList == null || configList.isEmpty()) {
            return 0;
        }

        int count = 0;

        for (EMaterialWarningConfigDTO config : configList) {
            if (config.getMaterialId() == null || config.getWarningThreshold() == null) {
                continue;
            }

            // 当前库存 = 未出库数量汇总
            BigDecimal currentStock = eMaterialWarehouseInDetailMapper.getStockQuantity(config.getMaterialId(), null);
            if (currentStock == null) {
                currentStock = BigDecimal.ZERO;
            }

            // 阈值大于库存才触发预警
            if (config.getWarningThreshold().compareTo(currentStock) > 0) {
                // 有未处理预警就不重复插
                Long exists = eMaterialWarningRecordMapper.countUnhandledByMaterialId(config.getMaterialId());
                if (exists != null && exists > 0) {
                    continue;
                }

                EMaterialWarningRecordPO recordPO = new EMaterialWarningRecordPO();
                recordPO.setId(snowflake.nextId());
                recordPO.setMaterialId(config.getMaterialId());
                recordPO.setMaterialName(config.getMaterialName());
                recordPO.setCurrentStock(currentStock);
                recordPO.setWarningThreshold(config.getWarningThreshold());
                recordPO.setReceivers(config.getReceivers());
                recordPO.setHandleStatus(0);

                eMaterialWarningRecordMapper.add(recordPO);
                count++;
            }
        }

        return count;
    }
}
