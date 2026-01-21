package com.yy.framework.flowable.convert;

import cn.hutool.core.util.ArrayUtil;
import com.yy.common.flowable.utils.BeanUtils;
import com.yy.common.flowable.utils.BpmnModelUtils;
import com.yy.common.flowable.utils.DateUtils;
import com.yy.common.flowable.utils.JsonUtils;
import com.yy.ppm.flowable.bean.dto.*;
import com.yy.ppm.flowable.bean.po.BpmCategoryPO;
import com.yy.ppm.flowable.bean.po.BpmFormPO;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.spring.security.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.yy.common.flowable.utils.CollectionUtils.convertList;


/**
 * 流程模型 Convert
 * 数据转换器（Converter），主要用于在 Flowable 流程模型对象和你的业务DTO之间进行转换
 * 流程模型涉及的内容较多，挑选重要key展示在DTO中。
 *
 * @author yunlongn
 */
@Mapper
public interface BpmModelConvert {

    BpmModelConvert INSTANCE = Mappers.getMapper(BpmModelConvert.class);

    default List<BpmModelDTO> buildModelList(List<Model> list,
                                             Map<Long, BpmFormPO> formMap,
                                             Map<String, BpmCategoryPO> categoryMap,
                                             Map<String, Deployment> deploymentMap,
                                             Map<String, ProcessDefinition> processDefinitionMap,
                                             Map<Long, SysUserDTO> userMap,
                                             Map<Long, SysDeptDTO> deptMap) {
        List<BpmModelDTO> result = convertList(list, model -> {
            BpmModelMetaInfoDTO metaInfo = parseMetaInfo(model);
            BpmFormPO form = metaInfo != null ? formMap.get(metaInfo.getFormId()) : null;
            BpmCategoryPO category = categoryMap.get(model.getCategory());
            Deployment deployment = model.getDeploymentId() != null ? deploymentMap.get(model.getDeploymentId()) : null;
            ProcessDefinition processDefinition = model.getDeploymentId() != null ?
                    processDefinitionMap.get(model.getDeploymentId()) : null;
            // 启动权限用户或者部门 先排除权限组
            List<SysUserDTO> startUsers = metaInfo != null ? convertList(metaInfo.getStartUserIds(), userMap::get) : null;
            List<SysDeptDTO> startDepts = metaInfo != null ? convertList(metaInfo.getStartDeptIds(), deptMap::get) : null;
            return buildModel0(model, metaInfo, form, category, deployment, processDefinition,startUsers,startDepts);
        });
        // 排序
        result.sort(Comparator.comparing(BpmModelMetaInfoDTO::getSort));
        return result;
    }

    default BpmModelDTO buildModel(Model model, byte[] bpmnBytes) {
        BpmModelMetaInfoDTO metaInfo = parseMetaInfo(model);
        BpmModelDTO modelVO = buildModel0(model, metaInfo, null, null, null, null,null,null);
        if (ArrayUtil.isNotEmpty(bpmnBytes)) {
            modelVO.setBpmnXml(BpmnModelUtils.getBpmnXml(bpmnBytes));
        }
        // 排除钉钉设计模型
//        modelVO.setSimpleModel(simpleModel);
        return modelVO;
    }

    default BpmModelDTO buildModel0(Model model,
                                    BpmModelMetaInfoDTO metaInfo, BpmFormPO form, BpmCategoryPO category,
                                    Deployment deployment, ProcessDefinition processDefinition, List<SysUserDTO> startUsers, List<SysDeptDTO> startDepts) {
        BpmModelDTO modelRespVO = new BpmModelDTO().setId(model.getId()).setName(model.getName())
                .setKey(model.getKey()).setCategory(model.getCategory())
                .setCreateTime(model.getCreateTime());
        // Form
        BeanUtils.copyProperties(metaInfo, modelRespVO);
        if (form != null) {
            modelRespVO.setFormName(form.getName());
        }
        // Category
        if (category != null) {
            modelRespVO.setCategoryName(category.getName());
        }
        // ProcessDefinition
        if (processDefinition != null) {
            modelRespVO.setProcessDefinition(BeanUtils.toBean(processDefinition, BpmProcessDefinitionDTO.class));
            modelRespVO.getProcessDefinition().setSuspensionState(processDefinition.isSuspended() ?
                    SuspensionState.SUSPENDED.getStateCode() : SuspensionState.ACTIVE.getStateCode());
            if (deployment != null) {
                modelRespVO.getProcessDefinition().setDeploymentTime(deployment.getDeploymentTime());
            }
        }
        // User、Dept
        modelRespVO.setStartUsers(BeanUtils.toBean(startUsers, UserSimpleBaseDTO.class))
                .setStartDepts(BeanUtils.toBean(startDepts, DeptSimpleBaseDTO.class));
        return modelRespVO;
    }

    default void copyToModel(Model model, BpmModelDTO reqVO) {
        model.setName(reqVO.getName());
        model.setKey(reqVO.getKey());
        model.setCategory(reqVO.getCategory());
        model.setMetaInfo(JsonUtils.toJsonString(BeanUtils.toBean(reqVO, BpmModelMetaInfoDTO.class)));
    }

    default BpmModelMetaInfoDTO parseMetaInfo(Model model) {
        BpmModelMetaInfoDTO vo = JsonUtils.parseObject(model.getMetaInfo(), BpmModelMetaInfoDTO.class);
        if (vo == null) {
            return null;
        }
        if (vo.getManagerUserIds() == null) {
            vo.setManagerUserIds(Collections.emptyList());
        }
        if (vo.getStartUserIds() == null) {
            vo.setStartUserIds(Collections.emptyList());
        }
        // 如果为空，兜底处理，使用 createTime 创建时间
        if (vo.getSort() == null) {
            vo.setSort(model.getCreateTime().getTime());
        }
        return vo;
    }

}
