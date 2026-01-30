package com.yy.framework.flowable.listener;
import com.google.common.collect.ImmutableSet;
import com.yy.common.flowable.utils.FlowableUtils;
import com.yy.ppm.flowable.service.BpmProcessInstanceService;
import jakarta.annotation.Resource;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.FlowableCancelledEvent;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 监听 {@link ProcessInstance} 的状态变更，更新其对应的 status 状态
 *
 * 专门监控**流程实例（ProcessInstance）**层面的生命周期。
 * PROCESS_CREATED：流程刚创建时触发。
 * PROCESS_COMPLETED：整个流程正常跑完时触发。
 * PROCESS_CANCELLED：流程被人工强行终止或取消时触发。
 * @author jason
 */
@Component
public class BpmProcessInstanceEventListener extends AbstractFlowableEngineEventListener {

    public static final Set<FlowableEngineEventType> PROCESS_INSTANCE_EVENTS = ImmutableSet.<FlowableEngineEventType>builder()
            .add(FlowableEngineEventType.PROCESS_CREATED)
            .add(FlowableEngineEventType.PROCESS_COMPLETED)
            .add(FlowableEngineEventType.PROCESS_CANCELLED)
            .build();

    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private BpmProcessInstanceService processInstanceService;

    public BpmProcessInstanceEventListener(){
        super(PROCESS_INSTANCE_EVENTS);
    }

    @Override
    protected void processCreated(FlowableEngineEntityEvent event) {
        ProcessInstance processInstance = (ProcessInstance) event.getEntity();
        processInstanceService.processProcessInstanceCreated(processInstance);
    }

    @Override
    protected void processCompleted(FlowableEngineEntityEvent event) {
        ProcessInstance processInstance = (ProcessInstance) event.getEntity();
        processInstanceService.processProcessInstanceCompleted(processInstance);
    }

    @Override
    protected void processCancelled(FlowableCancelledEvent event) {
        // 特殊情况：当跳转到 EndEvent 流程实例未结束, 会执行 deleteProcessInstance 方法
        ProcessInstance processInstance = processInstanceService.getProcessInstance(event.getProcessInstanceId());
        if (processInstance != null) {
            processInstanceService.processProcessInstanceCompleted(processInstance);
        }
    }

}
