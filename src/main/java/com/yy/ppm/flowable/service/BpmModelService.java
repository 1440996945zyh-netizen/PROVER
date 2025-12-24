package com.yy.ppm.flowable.service;

import com.yy.ppm.flowable.bean.dto.BpmModelDTO;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.repository.Model;

import java.util.List;

/**
 * 流程模型接口
 *
 * @author yunlongn
 */
public interface BpmModelService {

    /**
     * 获得流程模型列表
     *
     * @param name 模型名称
     * @return 流程模型列表
     */
    List<Model> getModelList(String name);

    /**
     * 根据分类编码获得流程模型数量
     *
     * @param category 分类编码
     * @return 流程模型数量
     */
    Long getModelCountByCategory(String category);

    /**
     * 创建流程模型
     *
     * @param bpmModelDTO 创建信息
     * @return 创建的流程模型的编号
     */
    boolean createModel(BpmModelDTO bpmModelDTO);

    /**
     * 获得流程模块
     *
     * @param id 编号
     * @return 流程模型
     */
    Model getModel(String id);

    /**
     * 获得流程模型的 BPMN XML
     *
     * @param id 编号
     * @return BPMN XML
     */
    byte[] getModelBpmnXML(String id);

    /**
     * 修改流程模型的 BPMN XML
     *
     * @param id      编号
     * @param bpmnXml BPMN XML
     */
    void updateModelBpmnXml(String id, String bpmnXml);

    /**
     * 修改流程模型
     *
     * @param userId 用户编号
     * @param updateReqVO 更新信息
     */
    boolean updateModel(Long userId, BpmModelDTO updateReqVO);

    /**
     * 将流程模型，部署成一个流程定义
     *
     * @param userId 用户编号
     * @param id 编号
     */
    void deployModel(Long userId, String id);

    /**
     * 删除模型
     *
     * @param userId  用户编号
     * @param id 编号
     */
    void deleteModel(Long userId, String id);

    /**
     * 清理模型，包括流程实例
     *
     * @param userId  用户编号
     * @param id 编号
     */
    void cleanModel(Long userId, String id);

    /**
     * 修改模型的状态，实际更新的部署的流程定义的状态
     *
     * @param userId 用户编号
     * @param id    编号
     * @param state 状态
     */
    void updateModelState(Long userId, String id, Integer state);

    /**
     * 获得流程定义编号对应的 BPMN Model
     *
     * @param processDefinitionId 流程定义编号
     * @return BPMN Model
     */
    BpmnModel getBpmnModelByDefinitionId(String processDefinitionId);
}
