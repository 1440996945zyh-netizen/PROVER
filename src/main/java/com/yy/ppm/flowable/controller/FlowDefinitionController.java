package com.yy.ppm.flowable.controller;

import cn.hutool.core.util.ObjectUtil;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.flowable.bean.dto.FlowProcDefDto;
import com.yy.ppm.flowable.bean.po.FlowSaveXmlPO;
import com.yy.ppm.flowable.service.IFlowDefinitionService;
import com.yy.ppm.flowable.util.FlowableUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description 流程模型定义
 *
 * @author hukang
 * @date 2025-11-07 15:18:35
 */
@RestController
@RequestMapping(value = "/api/internal/dict")
@Validated
@Tag(name = "流程引擎.流程模型")
public class FlowDefinitionController {
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(FlowDefinitionController.class);

    @Autowired
    private IFlowDefinitionService flowDefinitionService;

    /**
     * 列表查询
     */
    @GetMapping(value = "/getList")
    public Map<String, Object> getList(FlowProcDefDto flowProcDefDto) {

        Pages<FlowProcDefDto> flowProcDefDtoList = flowDefinitionService.getList(flowProcDefDto);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(flowProcDefDtoList);

    }


    /**
     * 新增流程模型
     */
    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody FlowSaveXmlPO vo) {
        InputStream in = null;
        BpmnModel bpmnModel = FlowableUtils.getBpmnModel(vo.getXml());
        if (ObjectUtil.isEmpty(bpmnModel)) {
            throw new RuntimeException("获取模型设计失败！");
        }
        // 获取开始节点
        StartEvent startEvent = FlowableUtils.getStartEvent(bpmnModel);
        if (ObjectUtil.isNull(startEvent)) {
            return Response.FAIL.newBuilder().out("开始节点不存在，请检查流程设计是否有误！").toResult();
        }
        // 查看开始节点的后一个任务节点出口
        List<SequenceFlow> outgoingFlows = startEvent.getOutgoingFlows();
        if(Objects.isNull(outgoingFlows)) {
            return Response.FAIL.newBuilder().out("导入失败，流程配置错误！").toResult();
        }
        //遍历返回下一个节点信息
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            //类型自己判断（获取下个节点是任务节点）
            FlowElement targetFlowElement = outgoingFlow.getTargetFlowElement();
            //下个是节点
            if(targetFlowElement instanceof UserTask){ // 下个出口是用户任务，而且是要发起人节点才让保存

                if(StringUtils.equals(((UserTask) targetFlowElement).getAssignee(), "${INITIATOR}"))
                {
                    break;
                }
                else {
                    return Response.FAIL.newBuilder().out("导入失败，流程第一个用户任务节点必须是发起人节点").toResult();
                }
            }
        }
        try {
            in = new ByteArrayInputStream(vo.getXml().getBytes(StandardCharsets.UTF_8));
            String deployId = flowDefinitionService.importFile(vo.getName(), vo.getCategory(), in);
            // 导入成功,保存流程实例节点的表单关联信息
            flowDefinitionService.saveDeployForm(deployId, bpmnModel);

        } catch (Exception e) {
            LOGGER.error("导入失败:", e.getMessage());
            return Response.FAIL.newBuilder().out( e.getMessage()).toResult();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                LOGGER.error("关闭输入流出错", e.getMessage());
            }
        }
        return Response.SUCCESS.newBuilder().out("导入成功").toResult();
    }

    /**
     * 获取流程图（读取xml）
     */
    @GetMapping("/readXml/{deployId}")
    public Map<String, Object> readXml(@PathVariable("deployId") String deployId) {
        try {
            String xmlData = flowDefinitionService.readXml(deployId);
            return Response.SUCCESS.newBuilder().out("查询成功").toResult(xmlData);
        } catch (Exception e) {
            return Response.FAIL.newBuilder().out("加载xml文件异常").toResult();
        }
    }

    /**
     * 激活或挂起流程
     */
    @PutMapping(value = "/updateState")
    public Map<String, Object> updateState(@RequestParam Integer state,@RequestParam String deployId) {
        flowDefinitionService.updateState(state, deployId);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

    /**
     * 删除流程
     * @param deployId
     * @return
     */
    @DeleteMapping(value = "/delete")
    public Map<String, Object> delete(@RequestParam String deployId) {
        flowDefinitionService.delete(deployId);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }



}
