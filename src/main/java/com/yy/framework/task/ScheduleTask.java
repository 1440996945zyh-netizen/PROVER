package com.yy.framework.task;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.yy.common.util.SpringContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.yy.ppm.customs.service.TCustomsService;
import com.yy.ppm.dispatch.service.TDisPortDaynightplanService;

@Component
@EnableScheduling
public class ScheduleTask implements SchedulingConfigurer {

    @Autowired
    private TCustomsService tCustomsService;

    @Autowired
    private TDisPortDaynightplanService tDisPortDaynightplanService;


    private void customsScheduleTask() {
        tCustomsService.plan2customsXML();
    }

    private void tosToBoHaiTongDayNightPlanTask() {
    	tDisPortDaynightplanService.tosToBoHaiTongDayNightPlanTask(null);
    }

    private void pushToCustoms() {
//        vehicleReservationService.pushToCustomer();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {

//    	scheduledTaskRegistrar.setScheduler(taskExecutor());
//
//        scheduledTaskRegistrar.addTriggerTask(
//                () -> customsScheduleTask(),
//                triggerContext -> {
//                    String cron = "*/30 * * * * ?";
//                    		//commonMapper.getCron(new Long(1));
//                    if (StringUtils.isEmpty(cron)) {
//                        System.out.println("cron is null");
//                    }
//                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
//                }
//        );
//        scheduledTaskRegistrar.addTriggerTask(
//                () -> pushToCustoms(),
//                triggerContext -> {
//                    String cron = "0 */30 * * * ?";
//                    		//commonMapper.getCron(new Long(1));
//                    if (StringUtils.isEmpty(cron)) {
//                        System.out.println("cron is null");
//                    }
//                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
//                }
//        );
//        scheduledTaskRegistrar.addTriggerTask(
//                () -> tosToBoHaiTongDayNightPlanTask(),
//                triggerContext -> {
//                    String cron = "0 0 18 * * ?";
//                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
//                }
//        );
//        scheduledTaskRegistrar.addTriggerTask(
//                () -> tosToBoHaiTongDayNightPlanTask(),
//                triggerContext -> {
//                    String cron = "0 0 08 * * ?";
//                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
//                }
//        );
    }

    // 自定义线程池
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(2);
    }
}
