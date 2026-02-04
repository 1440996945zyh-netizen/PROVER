package com.yy.ppm.flowable.controller;

import cn.hutool.core.collection.CollUtil;
import com.yy.common.enums.Response;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.flowable.convert.BpmModelConvert;
import com.yy.ppm.flowable.bean.dto.BpmModelMetaInfoDTO;
import com.yy.ppm.flowable.bean.dto.BpmModelDTO;
import com.yy.ppm.flowable.bean.po.BpmCategoryPO;
import com.yy.ppm.flowable.bean.po.BpmFormPO;
import com.yy.ppm.flowable.service.BpmCategoryService;
import com.yy.ppm.flowable.service.BpmFormService;
import com.yy.ppm.flowable.service.BpmModelService;
import com.yy.ppm.flowable.service.BpmProcessDefinitionService;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.service.SysDeptService;
import com.yy.ppm.system.service.SysUserService;
import jakarta.annotation.Resource;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Stream;

import static com.yy.common.flowable.utils.CollectionUtils.*;
import static com.yy.common.util.SecurityUtils.getLoginUserId;

/**
 *  流程模型
 */
@RestController
@RequestMapping("/bpm/model")
@Validated
public class BpmModelController {
    @Resource
    private BpmModelService modelService;
    @Resource
    private BpmFormService formService;

    @Resource
    private BpmCategoryService categoryService;

    @Resource
    private BpmProcessDefinitionService processDefinitionService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysDeptService sysDeptService;

    /**
     * 获取流程模型
     * @param map
     * @return
     */
    @GetMapping("/list")
//    public Map<String, Object> getModelList(HashMap<String,String> map) {
        public Map<String, Object> getModelList(@RequestParam(value = "name", required = false) String name,@RequestParam(value = "status", required = false) String status) {
        List<Model> list = modelService.getModelList(name);
        if (CollUtil.isEmpty(list)) {
            return Response.SUCCESS.newBuilder().toResult(list);
        }

        // 获得 Form 表单
        Set<Long> formIds = convertSet(list, model -> {
            BpmModelMetaInfoDTO metaInfo = BpmModelConvert.INSTANCE.parseMetaInfo(model);
            return metaInfo != null ? metaInfo.getFormId() : null;
        });
        Map<Long, BpmFormPO> formMap = formService.getFormMap(formIds);
        // 获得 Category Map
        // convertSet从对象中提取set<String>类型的某个字段
        Map<String, BpmCategoryPO> categoryMap = categoryService.getCategoryMap(
                convertSet(list, Model::getCategory));
        // 获得 Deployment Map
        Map<String, Deployment> deploymentMap = processDefinitionService.getDeploymentMap(
                convertSet(list, Model::getDeploymentId));
        // 获得 ProcessDefinition Map
        List<ProcessDefinition> processDefinitions = processDefinitionService.getProcessDefinitionListByDeploymentIds(
                deploymentMap.keySet());
        Map<String, ProcessDefinition> processDefinitionMap = convertMap(processDefinitions, ProcessDefinition::getDeploymentId);
        // 获得 User Map、Dept Map
        Set<Long> userIds = convertSetByFlatMap(list, model -> {
            BpmModelMetaInfoDTO metaInfo = BpmModelConvert.INSTANCE.parseMetaInfo(model);
            return metaInfo != null ? metaInfo.getStartUserIds().stream() : Stream.empty();
        });
        Map<Long, SysUserDTO> userMap = CollUtil.isEmpty(userIds) ? Map.of() : sysUserService.getUserMap(userIds);

        Set<Long> deptIds = convertSetByFlatMap(list, model -> {
            BpmModelMetaInfoDTO metaInfo = BpmModelConvert.INSTANCE.parseMetaInfo(model);
            return metaInfo != null && metaInfo.getStartDeptIds() != null ? metaInfo.getStartDeptIds().stream() : Stream.empty();
        });
        Map<Long, SysDeptDTO> deptMap = CollUtil.isEmpty(deptIds) ? Map.of() : sysDeptService.getDeptMap(deptIds);

        List<BpmModelDTO> bpmModelDTOS = BpmModelConvert.INSTANCE.buildModelList(list,
                formMap, categoryMap, deploymentMap, processDefinitionMap,userMap,deptMap);

        //状态查询条件
        List<BpmModelDTO> result = new ArrayList<>();
        if(!StringUtil.isEmpty(status)){
            for (BpmModelDTO bpmModelDTO : bpmModelDTOS) {
                if(!StringUtil.isEmpty(bpmModelDTO.getProcessDefinition())
                        && bpmModelDTO.getProcessDefinition().getSuspensionState().equals(Integer.parseInt(status))){
                    result.add(bpmModelDTO);
                }
            }
            return Response.SUCCESS.newBuilder().toResult(result);
        }

        return Response.SUCCESS.newBuilder().toResult(bpmModelDTOS);
    }

    /**
     * 查看详情
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    public Map<String, Object> getModel(String id) {
        Model model = modelService.getModel(id);
        if (model == null) {
            return Response.SUCCESS.newBuilder().toResult();
        }
        byte[] bpmnBytes = modelService.getModelBpmnXML(id);
        BpmModelDTO bpmModelDTO = BpmModelConvert.INSTANCE.buildModel(model, bpmnBytes);

        return Response.SUCCESS.newBuilder().toResult(bpmModelDTO);
    }

    /**
     * 保存流程模型
     * @param bpmModelDTO
     * @return
     */
    @PostMapping("/insert")
    public Map<String, Object> createModel(@RequestBody BpmModelDTO bpmModelDTO) {
        boolean flag = modelService.createModel(bpmModelDTO);
        return Response.SUCCESS.newBuilder().out(flag? "保存成功":"保存失败").toResult();
    }

    /**
     * 修改模型
     * @param modelVO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> updateModel(@RequestBody BpmModelDTO modelVO) {
        boolean flag = modelService.updateModel(getLoginUserId(), modelVO);
        return Response.SUCCESS.newBuilder().out(flag? "修改成功":"修改失败").toResult();
    }

    /**
     * 部署模型
     * @param id
     * @return
     */
    @PostMapping("/deploy")
    public Map<String, Object> deployModel(@RequestParam("id") String id) {
        modelService.deployModel(getLoginUserId(), id);
        return Response.SUCCESS.newBuilder().out("部署成功").toResult();
    }

    /**
     * 修改模型的状态（停用/启用）
     * @param reqVO
     * @return
     */
    @PutMapping("/updateState")
    public Map<String, Object> updateModelState(@RequestBody BpmModelDTO reqVO) {
        modelService.updateModelState(getLoginUserId(), reqVO.getId(), reqVO.getState());
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

    /**
     * 修改模型的 BPMN
     */
    @PutMapping("/updateBpmn")
    @PreAuthorize("@ss.hasPermission('bpm:model:update')")
    public Map<String, Object> updateModelBpmn(@RequestBody BpmModelDTO reqVO) {
        modelService.updateModelBpmnXml(reqVO.getId(), reqVO.getBpmnXml());
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     *删除模型
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteModel(@PathVariable("id") String id) {
        modelService.deleteModel(getLoginUserId(), id);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 清理模型
     * @param id
     * @return
     */
    @DeleteMapping("/clean")
    public Map<String, Object> cleanModel(@RequestParam("id") String id) {
        modelService.cleanModel(getLoginUserId(), id);
        return Response.SUCCESS.newBuilder().out("清理成功").toResult();
    }
}

