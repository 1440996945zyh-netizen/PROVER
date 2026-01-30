package com.yy.ppm.flowable.service.impl;

import cn.hutool.core.collection.CollUtil;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.yy.common.flowable.enums.BpmModelFormTypeEnum;
import com.yy.common.flowable.enums.BpmModelTypeEnum;
import com.yy.common.flowable.enums.BpmReasonEnum;
import com.yy.common.flowable.enums.BpmTaskCandidateStrategyEnum;
import com.yy.common.flowable.utils.BpmnModelUtils;
import com.yy.common.flowable.utils.CollectionUtils;
import com.yy.common.flowable.utils.ValidationUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.flowable.convert.BpmModelConvert;
import com.yy.framework.flowable.strategy.BpmTaskCandidateInvoker;
import com.yy.ppm.flowable.bean.dto.BpmBusinessConfigDTO;
import com.yy.ppm.flowable.bean.dto.BpmModelMetaInfoDTO;
import com.yy.ppm.flowable.bean.dto.BpmModelDTO;
import com.yy.ppm.flowable.bean.po.BpmFormPO;
import com.yy.ppm.flowable.mapper.BpmBusinessConfigMapper;
import com.yy.ppm.flowable.service.BpmFormService;
import com.yy.ppm.flowable.service.BpmModelService;
import com.yy.ppm.flowable.service.BpmProcessDefinitionService;
import com.yy.ppm.flowable.service.BpmProcessInstanceCopyService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ModelQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.Objects;

import static com.yy.common.flowable.constants.ErrorCodeConstants.*;
import static com.yy.common.flowable.utils.BpmnModelUtils.parseCandidateStrategy;
import static com.yy.common.flowable.utils.ServiceExceptionUtil.exception;


/**
 * 流程模型实现：主要进行 Flowable {@link Model} 的维护
 *
 * @author yunlongn
 * @author 芋道源码
 * @author jason
 */
@Service
@Validated
@Slf4j
public class BpmModelServiceImpl implements BpmModelService {

    @Resource
    private RepositoryService repositoryService;
    @Resource
    private BpmProcessDefinitionService processDefinitionService;
    @Resource
    private BpmFormService bpmFormService;

    @Resource
    private BpmTaskCandidateInvoker taskCandidateInvoker;

    @Resource
    private HistoryService historyService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private BpmProcessInstanceCopyService processInstanceCopyService;
    @Resource
    private BpmBusinessConfigMapper bpmBusinessConfigMapper;

    /**
     * 列表查询
     * @param name 模型名称
     * @return
     */
    @Override
    public List<Model> getModelList(String name) {
        ModelQuery modelQuery = repositoryService.createModelQuery();
        if (StrUtil.isNotEmpty(name)) {
            modelQuery.modelNameLike("%" + name + "%");
        }
        // 租户查询不需要
//        modelQuery.modelTenantId(FlowableUtils.getTenantId());
        return modelQuery.list();
    }

    @Override
    public Long getModelCountByCategory(String category) {
        return repositoryService.createModelQuery()
                .modelCategory(category)
//                .modelTenantId(FlowableUtils.getTenantId())
                .count();
    }

    /**
     * 保存
     * @param createReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createModel(BpmModelDTO createReqVO) {
        // 流程标识格式校验
        if (!ValidationUtils.isXmlNCName(createReqVO.getKey())) {
            throw exception(MODEL_KEY_VALID);
        }
        // 1. 校验流程标识已经存在
        Model keyModel = getModelByKey(createReqVO.getKey());
        if (keyModel != null) {
            throw exception(MODEL_KEY_EXISTS, createReqVO.getKey());
        }

        // 2. 创建 Model 对象
        createReqVO.setSort(System.currentTimeMillis()); // 使用当前时间，作为排序
        Model model = repositoryService.newModel();
        BpmModelConvert.INSTANCE.copyToModel(model, createReqVO);
//        model.setTenantId(FlowableUtils.getTenantId());

        // 3. 保存模型
        saveModel(model, createReqVO);
        return StringUtil.isEmpty(model.getId()) ? false:true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateModel(Long userId, BpmModelDTO updateReqVO) {
        // 1. 校验流程模型存在
        Model model = validateModelManager(updateReqVO.getId(), userId);

        // 2. 填充 Model 信息
        BpmModelConvert.INSTANCE.copyToModel(model, updateReqVO);

        // 3. 保存模型
        saveModel(model, updateReqVO);
        return StringUtil.isEmpty(model.getId()) ? false:true;
    }

    /**
     * 保存模型的基本信息、流程图
     *
     * @param model 模型
     * @param saveReqVO 保存信息
     */
    private void saveModel(Model model, BpmModelDTO saveReqVO) {
        // 1. 保存模型的基础信息
        repositoryService.saveModel(model);
        // 系统只考虑BPMN 设计器
        updateModelBpmnXml(model.getId(), saveReqVO.getBpmnXml());
    }



    private Model validateModelExists(String id) {
        Model model = repositoryService.getModel(id);
        if (model == null) {
            throw exception(MODEL_NOT_EXISTS);
        }
        return model;
    }

    /**
     * 校验是否有流程模型的管理权限
     *
     * @param id     流程模型编号
     * @param userId 用户编号
     * @return 流程模型
     */
    private Model validateModelManager(String id, Long userId) {
        Model model = validateModelExists(id);
        BpmModelMetaInfoDTO metaInfo = BpmModelConvert.INSTANCE.parseMetaInfo(model);
        if (metaInfo == null || !CollUtil.contains(metaInfo.getManagerUserIds(), userId)) {
            throw exception(MODEL_UPDATE_FAIL_NOT_MANAGER, model.getName());
        }
        return model;
    }

    /**
     * 将流程模型，部署成一个流程定义
     *
     * @param userId 用户编号
     * @param id 编号
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 因为进行多个操作，所以开启事务
    public void deployModel(Long userId, String id) {
        // 1.1 校验流程模型存在
        Model model = validateModelManager(id, userId);
        BpmModelMetaInfoDTO metaInfo = BpmModelConvert.INSTANCE.parseMetaInfo(model);
        // 1.2 校验流程图（流程图规则）
        byte[] bpmnBytes = getModelBpmnXML(model.getId());
        validateBpmnXml(bpmnBytes, metaInfo.getType());
        // 1.3 校验表单已配
        BpmFormPO form = validateFormConfig(metaInfo);
        // 1.4 校验任务分配规则已配置(后续自动分配任务给对应负责人校验)
        taskCandidateInvoker.validateBpmnConfig(bpmnBytes);
        // 1.5 获取仿钉钉流程设计器模型数据
//        String simpleJson = getModelSimpleJson(model.getId());

        // 2.1 创建流程定义
        String definitionId = processDefinitionService.createProcessDefinition(model, metaInfo, bpmnBytes,
                form);

        // 2.2 将老的流程定义进行挂起。也就是说，只有最新部署的流程定义，才可以发起任务。
        updateProcessDefinitionSuspended(model.getDeploymentId());

        // 2.3 更新 model 的 deploymentId，进行关联
        ProcessDefinition definition = processDefinitionService.getProcessDefinition(definitionId);
        model.setDeploymentId(definition.getDeploymentId());
        repositoryService.saveModel(model);

        // 查询是否关联业务，若关联业务则将业务对应的流程定义更新为最新
        List<BpmBusinessConfigDTO> businessConfigCount = bpmBusinessConfigMapper.getBusinessConfigCount(id.toString());
        if (CollectionUtil.isEmpty(businessConfigCount)) {
            for (BpmBusinessConfigDTO bpmBusinessConfigDTO : businessConfigCount) {
                bpmBusinessConfigDTO.setProcDefId(definitionId);
                bpmBusinessConfigMapper.update(bpmBusinessConfigDTO);
            }
        }
    }

    private void validateBpmnXml(byte[] bpmnBytes, Integer type) {
        BpmnModel bpmnModel = BpmnModelUtils.getBpmnModel(bpmnBytes);
        if (bpmnModel == null) {
            throw exception(MODEL_NOT_EXISTS);
        }
        // 1. 没有 StartEvent
        StartEvent startEvent = BpmnModelUtils.getStartEvent(bpmnModel);
        if (startEvent == null) {
            throw exception(MODEL_DEPLOY_FAIL_BPMN_START_EVENT_NOT_EXISTS);
        }
        // 2. 校验 UserTask 的 name 都配置了
        List<UserTask> userTasks = BpmnModelUtils.getBpmnModelElements(bpmnModel, UserTask.class);
        userTasks.forEach(userTask -> {
            if (StrUtil.isEmpty(userTask.getName())) {
                throw exception(MODEL_DEPLOY_FAIL_BPMN_USER_TASK_NAME_NOT_EXISTS, userTask.getId());
            }
        });
        // 3. 校验第一个用户任务节点的规则类型是否为“审批人自选”，BPMN 设计器，校验第一个用户任务节点，SIMPLE 设计器，第一个节点固定为发起人所以校验第二个用户任务节点
        UserTask firUserTask = CollUtil.get(userTasks, BpmModelTypeEnum.BPMN.getType().equals(type) ? 0 : 1);
        if (firUserTask == null) {
            return;
        }
        Integer candidateStrategy = parseCandidateStrategy(firUserTask);
        if (Objects.equals(candidateStrategy, BpmTaskCandidateStrategyEnum.APPROVE_USER_SELECT.getStrategy())) {
            throw exception(MODEL_DEPLOY_FAIL_FIRST_USER_TASK_CANDIDATE_STRATEGY_ERROR, firUserTask.getName());
        }
    }

    /**
     * 删除模型
     *
     * @param userId  用户编号
     * @param id 编号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModel(Long userId, String id) {
        // 校验流程模型存在
        Model model = validateModelManager(id, userId);

        // 执行删除
        repositoryService.deleteModel(id);
        // 禁用流程定义
        updateProcessDefinitionSuspended(model.getDeploymentId());
    }

    /**
     * 清理模型，包括流程实例
     *
     * @param userId  用户编号
     * @param id 编号
     */
    @Override
    public void cleanModel(Long userId, String id) {
        // 1. 校验流程模型存在
        Model model = validateModelManager(id, userId);

        // 2. 清理所有流程数据
        // 2.1 先取消所有正在运行的流程
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(model.getKey()).list();
        processInstances.forEach(processInstance -> {
            runtimeService.deleteProcessInstance(processInstance.getId(),
                    BpmReasonEnum.CANCEL_BY_SYSTEM.getReason());
            historyService.deleteHistoricProcessInstance(processInstance.getId());
            processInstanceCopyService.deleteProcessInstanceCopy(processInstance.getId());
        });
        // 2.2 再从历史中删除所有相关的流程数据
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(model.getKey()).list();
        historicProcessInstances.forEach(historicProcessInstance -> {
            historyService.deleteHistoricProcessInstance(historicProcessInstance.getId());
            processInstanceCopyService.deleteProcessInstanceCopy(historicProcessInstance.getId());
        });
        // 2.3 清理所有 Task
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(model.getKey()).list();
        tasks.forEach(task -> taskService.deleteTask(task.getId(),BpmReasonEnum.CANCEL_BY_PROCESS_CLEAN.getReason()));
    }

    @Override
    public void updateModelState(Long userId, String id, Integer state) {
        // 1.1 校验流程模型存在
        Model model = validateModelManager(id, userId);
        // 1.2 校验流程定义存在
        ProcessDefinition definition = processDefinitionService
                .getProcessDefinitionByDeploymentId(model.getDeploymentId());
        if (definition == null) {
            throw exception(PROCESS_DEFINITION_NOT_EXISTS);
        }

        // 2. 更新状态
        processDefinitionService.updateProcessDefinitionState(definition.getId(), state);
    }

    @Override
    public BpmnModel getBpmnModelByDefinitionId(String processDefinitionId) {
        return repositoryService.getBpmnModel(processDefinitionId);
    }



    /**
     * 校验流程表单已配置
     *
     * @param metaInfo 流程模型元数据
     * @return 表单配置
     */
    private BpmFormPO validateFormConfig(BpmModelMetaInfoDTO metaInfo) {
        if (metaInfo == null || metaInfo.getFormType() == null) {
            throw exception(MODEL_DEPLOY_FAIL_FORM_NOT_CONFIG);
        }
        // 校验表单存在
        if (Objects.equals(metaInfo.getFormType(), BpmModelFormTypeEnum.NORMAL.getType())) {
            if (metaInfo.getFormId() == null) {
                throw exception(MODEL_DEPLOY_FAIL_FORM_NOT_CONFIG);
            }
            BpmFormPO form = bpmFormService.getDetail(metaInfo.getFormId());
            if (form == null) {
                throw exception(FORM_NOT_EXISTS);
            }
            return form;
        } else {
            if (StrUtil.isEmpty(metaInfo.getFormCustomCreatePath())
                    || StrUtil.isEmpty(metaInfo.getFormCustomViewPath())) {
                throw exception(MODEL_DEPLOY_FAIL_FORM_NOT_CONFIG);
            }
            return null;
        }
    }

    /**
     * 修改流程模型的 BPMN XML
     *
     * @param id      编号
     * @param bpmnXml BPMN XML
     */
    @Override
    public void updateModelBpmnXml(String id, String bpmnXml) {
        if (StrUtil.isEmpty(bpmnXml)) {
            return;
        }
        repositoryService.addModelEditorSource(id, StrUtil.utf8Bytes(bpmnXml));
    }


//    @SuppressWarnings("JavaExistingMethodCanBeUsed")
//    private String getModelSimpleJson(String id) {
//        byte[] bytes = repositoryService.getModelEditorSourceExtra(id);
//        if (ArrayUtil.isEmpty(bytes)) {
//            return null;
//        }
//        return StrUtil.utf8Str(bytes);
//    }


    /**
     * 挂起 deploymentId 对应的流程定义
     * <p>
     * 注意：这里一个 deploymentId 只关联一个流程定义
     *
     * @param deploymentId 流程发布Id
     */
    private void updateProcessDefinitionSuspended(String deploymentId) {
        if (StrUtil.isEmpty(deploymentId)) {
            return;
        }
        ProcessDefinition oldDefinition = processDefinitionService.getProcessDefinitionByDeploymentId(deploymentId);
        if (oldDefinition == null) {
            return;
        }
        processDefinitionService.updateProcessDefinitionState(oldDefinition.getId(),
                SuspensionState.SUSPENDED.getStateCode());
    }

    private Model getModelByKey(String key) {
        return repositoryService.createModelQuery()
//                .modelTenantId(FlowableUtils.getTenantId())
                .modelKey(key).singleResult();
    }

    @Override
    public Model getModel(String id) {
        return repositoryService.getModel(id);
    }

    @Override
    public byte[] getModelBpmnXML(String id) {
        return repositoryService.getModelEditorSource(id);
    }

}
