package com.yy.ppm.equipment.scheduledTask;

import com.yy.common.log.MicroLogger;
import com.yy.ppm.equipment.service.impl.EMaterialWarningConfigServiceImpl;
import org.springframework.scheduling.annotation.Scheduled;

public class Task {
    private static final MicroLogger LOGGER = new MicroLogger(EMaterialWarningConfigServiceImpl.class);
    private EMaterialWarningConfigServiceImpl eMaterialWarningConfig;

    /**
     * 物料预警消息：每天零点跑一次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduledGenerateWarningRecord() {
        LOGGER.enter("物资预警定时任务开始");
        Integer count = eMaterialWarningConfig.generateWarningRecord();
        LOGGER.exit("物资预警定时任务结束, count:" + count);
    }
}
