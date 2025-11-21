package com.yy.ppm.flowable.service;

import com.yy.common.page.Pages;
import com.yy.ppm.flowable.bean.dto.FlowProcDefDto;
import org.flowable.bpmn.model.BpmnModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @Description: 流程定义
 * @Author: hukang
 * @Date:   2025-11-07
 * @Version: V1.0
 */
public interface IFlowDefinitionService {
    /**
     * 导入流程文件
     *
     * @param name
     * @param category
     * @param in
     * @return deployId
     */
    String importFile(String name, String category, InputStream in);

    /**
     * 保存发布流程时新增流程实例关联表单
     *
     * @param deployId,bpmnModel 流程实例关联表单
     * @return 结果
     */
    boolean saveDeployForm(String deployId, BpmnModel bpmnModel);

    /**
     * 列表查询
     */
    Pages<FlowProcDefDto> getList(FlowProcDefDto flowProcDefDto);

    /**
     * 获取流程图（读取xml）
     */
    String readXml(String deployId) throws IOException;;

    /**
     * 激活或挂起流程
     */
    void updateState(Integer state, String deployId);

    /**
     * 删除流程
     * @param deployId
     * @return
     */
    void delete(String deployId);
}
