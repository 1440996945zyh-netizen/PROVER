package com.yy.common.util;
import com.yy.common.enums.Constants;
import org.quartz.CronExpression;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.text.ParseException;
import java.util.Date;

/**
 * job工具类
 * @author hukang
 */
@Component
public class JobUtil {

    @Autowired
    private Scheduler scheduler;

    /**
     * 根据jobName和jobGroup生成triggerKey
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    public TriggerKey getTriggerKeyByJob(String jobName, String jobGroup) {
        String triggerName = Constants.TRIGGER_PREFIX + jobName;
        String triggerGroup = Constants.TRIGGER_PREFIX + jobGroup;
        return TriggerKey.triggerKey(triggerName, triggerGroup);
    }

    /**
     * 获取job状态
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    public String getJobStatusInfo(String jobName, String jobGroup) {
        String jobStatusInfo = "";
        TriggerKey triggerKey = getTriggerKeyByJob(jobName, jobGroup);
        try {
            jobStatusInfo = scheduler.getTriggerState(triggerKey).name();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
        return jobStatusInfo;
    }

    /**
     * 根据cron表达式获取下次执行时间
     *
     * @param cronExpression
     * @return
     */
    public Date getNextFireDate(String cronExpression) {
        try {
            CronExpression cron = new CronExpression(cronExpression);
            Date nextFireDate = cron.getNextValidTimeAfter(new Date());
            return nextFireDate;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
