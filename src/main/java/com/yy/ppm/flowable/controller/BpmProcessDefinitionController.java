package com.yy.ppm.flowable.controller;

import cn.hutool.core.collection.CollUtil;
import com.yy.common.enums.Response;
import com.yy.common.page.Pages;
import com.yy.framework.flowable.convert.BpmProcessDefinitionConvert;
import com.yy.ppm.flowable.bean.dto.BpmProcessDefinitionDTO;
import com.yy.ppm.flowable.bean.dto.BpmProcessDefinitionInfoSearchDTO;
import com.yy.ppm.flowable.bean.po.BpmCategoryPO;
import com.yy.ppm.flowable.bean.po.BpmFormPO;
import com.yy.ppm.flowable.bean.po.BpmProcessDefinitionInfoPO;
import com.yy.ppm.flowable.service.BpmCategoryService;
import com.yy.ppm.flowable.service.BpmFormService;
import com.yy.ppm.flowable.service.BpmProcessDefinitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.yy.common.flowable.utils.CollectionUtils.convertList;
import static com.yy.common.flowable.utils.CollectionUtils.convertSet;
import static com.yy.common.util.SecurityUtils.getLoginUserId;

/**
 * 流程定义
 */
@RestController
@RequestMapping("/bpm/processDefinition")
@Validated
public class BpmProcessDefinitionController {

    @Resource
    private BpmProcessDefinitionService processDefinitionService;
    @Resource
    private BpmFormService formService;
    @Resource
    private BpmCategoryService categoryService;

    /**
     * 获得流程定义分页（我的流程）
     * @param pageReqVO
     * @return
     */
    @GetMapping("/page")
    public Map<String, Object> getProcessDefinitionPage(BpmProcessDefinitionInfoSearchDTO pageReqVO) {
        Pages<ProcessDefinition> pageResult = processDefinitionService.getProcessDefinitionPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getPages())) {
            return Response.SUCCESS.newBuilder().toResult(pageResult);
        }

        // 获得 Category Map
        Map<String, BpmCategoryPO> categoryMap = categoryService.getCategoryMap(
                convertSet(pageResult.getPages(), ProcessDefinition::getCategory));
        // 获得 Deployment Map
        Map<String, Deployment> deploymentMap = processDefinitionService.getDeploymentMap(
                convertSet(pageResult.getPages(), ProcessDefinition::getDeploymentId));
        // 获得 BpmProcessDefinitionInfoPO Map
        Map<String, BpmProcessDefinitionInfoPO> processDefinitionMap = processDefinitionService.getProcessDefinitionInfoMap(
                convertSet(pageResult.getPages(), ProcessDefinition::getId));
        // 获得 Form Map
        Map<Long, BpmFormPO> formMap = formService.getFormMap(
               convertSet(processDefinitionMap.values(), BpmProcessDefinitionInfoPO::getFormId));
        Pages<BpmProcessDefinitionDTO> bpmProcessDefinitionDTOPages = BpmProcessDefinitionConvert.INSTANCE.buildProcessDefinitionPage(
                pageResult, deploymentMap, processDefinitionMap, formMap, categoryMap);
        return Response.SUCCESS.newBuilder().toResult(bpmProcessDefinitionDTOPages);
    }

    /**
     * 获得流程定义列表（发起流程功能列表查询）
     * @param suspensionState
     * @return
     */
    @GetMapping ("/list")
    public Map<String, Object> getProcessDefinitionList(@RequestParam("suspensionState") Integer suspensionState) {
        // 1.1 获得开启的流程定义
        List<ProcessDefinition> list = processDefinitionService.getProcessDefinitionListBySuspensionState(suspensionState);
        if (CollUtil.isEmpty(list)) {
            return Response.SUCCESS.newBuilder().toResult(list);
        }
        // 1.2 移除不可见的流程定义
        Map<String, BpmProcessDefinitionInfoPO> processDefinitionMap = processDefinitionService.getProcessDefinitionInfoMap(
                convertSet(list, ProcessDefinition::getId));
        Long userId = getLoginUserId();

        list.removeIf(processDefinition -> {
            BpmProcessDefinitionInfoPO processDefinitionInfo = processDefinitionMap.get(processDefinition.getId());
            return processDefinitionInfo == null // 不存在
                    || Boolean.FALSE.equals(processDefinitionInfo.getVisible()) // visible 不可见
                    || !processDefinitionService.canUserStartProcessDefinition(processDefinitionInfo, userId); // 无权限发起
        });

        // 2. 拼接 VO 返回
        List<BpmProcessDefinitionDTO> bpmProcessDefinitionDTOS = BpmProcessDefinitionConvert.INSTANCE.buildProcessDefinitionList(
                list, null, processDefinitionMap, null, null);
        return Response.SUCCESS.newBuilder().toResult(bpmProcessDefinitionDTOS);
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得流程定义精简列表", description = "只包含未挂起的流程，主要用于前端的下拉选项")
    public Map<String,Object> getSimpleProcessDefinitionList() {
        // 只查询未挂起的流程
        List<ProcessDefinition> list = processDefinitionService.getProcessDefinitionListBySuspensionState(
                SuspensionState.ACTIVE.getStateCode());
        // 拼接 VO 返回，只返回 id、name、key
        List<BpmProcessDefinitionDTO> bpmProcessDefinitionDTOS = convertList(list, definition -> new BpmProcessDefinitionDTO()
                .setId(definition.getId()).setName(definition.getName()).setKey(definition.getKey()));
        return Response.SUCCESS.newBuilder().toResult(bpmProcessDefinitionDTOS);
    }

    /**
     * 获得流程定义（发起流程详情）
     * @param id
     * @param key
     * @return
     */
    @GetMapping ("/getDetail")
    public Map<String, Object> getProcessDefinition(@RequestParam(value = "id", required = false) String id, @RequestParam(value = "key", required = false) String key) {
        ProcessDefinition processDefinition = id != null ? processDefinitionService.getProcessDefinition(id)
                : processDefinitionService.getActiveProcessDefinition(key);
        if (processDefinition == null) {
            return Response.SUCCESS.newBuilder().toResult();
        }
        BpmProcessDefinitionInfoPO processDefinitionInfo = processDefinitionService.getProcessDefinitionInfo(processDefinition.getId());
        BpmnModel bpmnModel = processDefinitionService.getProcessDefinitionBpmnModel(processDefinition.getId());
        //
        BpmProcessDefinitionDTO bpmProcessDefinitionDTO = BpmProcessDefinitionConvert.INSTANCE.buildProcessDefinition(
                processDefinition, null, processDefinitionInfo, null, null, bpmnModel);

        return Response.SUCCESS.newBuilder().toResult(bpmProcessDefinitionDTO);
    }

}
