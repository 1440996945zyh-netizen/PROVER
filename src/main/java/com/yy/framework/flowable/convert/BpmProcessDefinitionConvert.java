package com.yy.framework.flowable.convert;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import com.yy.common.flowable.utils.BeanUtils;
import com.yy.common.flowable.utils.BpmnModelUtils;
import com.yy.common.flowable.utils.CollectionUtils;
import com.yy.common.page.Pages;
import com.yy.common.util.PageConverterUtils;
import com.yy.ppm.flowable.bean.dto.BpmProcessDefinitionDTO;
import com.yy.ppm.flowable.bean.po.BpmCategoryPO;
import com.yy.ppm.flowable.bean.po.BpmFormPO;
import com.yy.ppm.flowable.bean.po.BpmProcessDefinitionInfoPO;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Bpm 流程定义的 Convert
 *
 * @author yunlong.li
 */
@Mapper
public interface BpmProcessDefinitionConvert {

    BpmProcessDefinitionConvert INSTANCE = Mappers.getMapper(BpmProcessDefinitionConvert.class);

    default Pages<BpmProcessDefinitionDTO> buildProcessDefinitionPage(Pages<ProcessDefinition> page,
                                                                           Map<String, Deployment> deploymentMap,
                                                                           Map<String, BpmProcessDefinitionInfoPO> processDefinitionInfoMap,
                                                                           Map<Long, BpmFormPO> formMap,
                                                                           Map<String, BpmCategoryPO> categoryMap) {
        List<BpmProcessDefinitionDTO> list = buildProcessDefinitionList(page.getPages(), deploymentMap, processDefinitionInfoMap, formMap, categoryMap);
        return PageConverterUtils.convert(list,page.getPageNum(),page.getPageSize(),page.getTotalNum());
    }

    default List<BpmProcessDefinitionDTO> buildProcessDefinitionList(List<ProcessDefinition> list,
                                                                     Map<String, Deployment> deploymentMap,
                                                                     Map<String, BpmProcessDefinitionInfoPO> processDefinitionInfoMap,
                                                                     Map<Long, BpmFormPO> formMap,
                                                                     Map<String, BpmCategoryPO> categoryMap) {
        List<BpmProcessDefinitionDTO> result = CollectionUtils.convertList(list, definition -> {
            Deployment deployment = MapUtil.get(deploymentMap, definition.getDeploymentId(), Deployment.class);
            BpmProcessDefinitionInfoPO processDefinitionInfo = MapUtil.get(processDefinitionInfoMap, definition.getId(), BpmProcessDefinitionInfoPO.class);
            BpmFormPO form = null;
            if (processDefinitionInfo != null) {
                form = MapUtil.get(formMap, processDefinitionInfo.getFormId(), BpmFormPO.class);
            }
            BpmCategoryPO category = MapUtil.get(categoryMap, definition.getCategory(), BpmCategoryPO.class);
            return buildProcessDefinition(definition, deployment, processDefinitionInfo, form, category, null);
        });
        // 排序
        result.sort(Comparator.comparing(BpmProcessDefinitionDTO::getSort));
        return result;
    }

    default BpmProcessDefinitionDTO buildProcessDefinition(ProcessDefinition definition,
                                                           Deployment deployment,
                                                           BpmProcessDefinitionInfoPO processDefinitionInfo,
                                                           BpmFormPO form,
                                                           BpmCategoryPO category,
                                                           BpmnModel bpmnModel) {
        BpmProcessDefinitionDTO respVO = BeanUtils.toBean(definition, BpmProcessDefinitionDTO.class);
        respVO.setSuspensionState(definition.isSuspended() ? SuspensionState.SUSPENDED.getStateCode() : SuspensionState.ACTIVE.getStateCode());
        // Deployment
        if (deployment != null) {
            respVO.setDeploymentTime(LocalDateTimeUtil.of(deployment.getDeploymentTime()));
        }
        // BpmProcessDefinitionInfoPO
        if (processDefinitionInfo != null) {
            copyTo(processDefinitionInfo, respVO);
            // Form
            if (form != null) {
                respVO.setFormName(form.getName());
            }
        }
        // Category
        if (category != null) {
            respVO.setCategoryName(category.getName());
        }
        // BpmnModel
        if (bpmnModel != null) {
            respVO.setBpmnXml(BpmnModelUtils.getBpmnXml(bpmnModel));
        }
        return respVO;
    }

    @Mapping(source = "from.id", target = "to.id", ignore = true)
    void copyTo(BpmProcessDefinitionInfoPO from, @MappingTarget BpmProcessDefinitionDTO to);

}
