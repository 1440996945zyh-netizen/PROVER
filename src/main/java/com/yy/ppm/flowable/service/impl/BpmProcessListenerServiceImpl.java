package com.yy.ppm.flowable.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import com.yy.common.enums.Response;
import com.yy.common.flowable.utils.BpmnModelUtils;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.framework.flowable.convert.BpmModelConvert;
import com.yy.ppm.flowable.bean.dto.BpmModelDTO;
import com.yy.ppm.flowable.bean.dto.BpmModelMetaInfoDTO;
import com.yy.ppm.flowable.bean.dto.BpmProcessListenerDTO;
import com.yy.ppm.flowable.bean.dto.BpmProcessListenerSearchDTO;
import com.yy.ppm.flowable.bean.po.BpmCategoryPO;
import com.yy.ppm.flowable.bean.po.BpmFormPO;
import com.yy.ppm.flowable.mapper.BpmProcessListenerMapper;
import com.yy.ppm.flowable.service.*;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.service.SysDeptService;
import com.yy.ppm.system.service.SysUserService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yy.common.flowable.enums.BpmProcessListenerTypeEnum;
import com.yy.common.flowable.enums.BpmProcessListenerValueTypeEnum;
import org.flowable.engine.delegate.TaskListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Stream;

import static com.yy.common.flowable.utils.CollectionUtils.*;
import static com.yy.common.flowable.utils.CollectionUtils.convertSetByFlatMap;

@Service
public class BpmProcessListenerServiceImpl implements BpmProcessListenerService {

    private static final MicroLogger LOGGER = new MicroLogger(BpmProcessListenerServiceImpl.class);

    @Autowired
    private Snowflake snowflake;

    @Resource
    private BpmProcessListenerMapper bpmProcessListenerMapper;
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
     * 分页查询列表
     */
    @Override
    public Pages<BpmProcessListenerDTO> getList(BpmProcessListenerSearchDTO searchDTO) {
        final String methodName = "BpmProcessListenerServiceImpl:getList";
        LOGGER.enter(methodName, "分页查询流程监听器");
        Pages<BpmProcessListenerDTO> pages = PageHelperUtils.limit(searchDTO,
                () -> bpmProcessListenerMapper.getList(searchDTO));

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return pages;
    }
    /**
     * 新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(BpmProcessListenerDTO dto) {
        final String methodName = "BpmProcessListenerServiceImpl:insert";
        LOGGER.enter(methodName, "新增监听器");
        // todo 校验逻辑
        // --- 开始集成校验逻辑 ---
        validateListenerValue(dto);
        // --- 校验逻辑结束 ---
        // 映射 DTO 到 PO
        mapDtoToPo(dto);
        dto.setId(snowflake.nextId());
        dto.setCreateTime(new Date());

        bpmProcessListenerMapper.insert(dto);
        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    /**
     * 修改
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BpmProcessListenerDTO dto) {
        final String methodName = "BpmProcessListenerServiceImpl:update";
        LOGGER.enter(methodName, "修改监听器");
        // 1. 校验记录是否存在
        validateProcessListenerExists(dto.getId());
        // todo 校验逻辑
        // --- 开始集成校验逻辑 ---
        // 2. 校验值和类型
        validateListenerValue(dto);
        // --- 校验逻辑结束 ---
        // 3. DTO 字段映射到 PO 字段，用于持久化
        mapDtoToPo(dto);
        bpmProcessListenerMapper.update(dto);
        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        final String methodName = "BpmProcessListenerServiceImpl:deleteById";
        LOGGER.enter(methodName, "删除监听器");
        if (id == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }
        Integer count = bpmProcessListenerMapper.deleteById(id);
        if (count <= 0) {
            throw new BusinessRuntimeException("删除失败，记录不存在");
        }
        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @Override
    public BpmProcessListenerDTO getDetail(Long id) {
        final String methodName = "BpmProcessListenerServiceImpl:getDetail";
        LOGGER.enter(methodName, "查询详情");
        BpmProcessListenerDTO dto = bpmProcessListenerMapper.getDetail(id);
        if (dto == null) {
            throw new BusinessRuntimeException("记录不存在");
        }
        LOGGER.exit(methodName, StringUtils.EMPTY);
        return dto;
    }


    /**
     * 将 DTO 的输入字段映射到 PO 的持久化字段
     * @param dto 监听器数据
     */
    private void mapDtoToPo(BpmProcessListenerDTO dto) {
        dto.setListenerName(dto.getListenerName()); // 监听器名字
        dto.setListenerStatus(dto.getListenerStatus()); // 监听器状态
        dto.setListenerTypeCode(dto.getListenerTypeCode()); // 监听器类型
        dto.setListenerTypeName(dto.getListenerTypeName()); // 监听器类型名称
        dto.setListenerEventCode(dto.getListenerEventCode()); // 监听事件
        dto.setListenerEventName(dto.getListenerEventName()); // 监听事件名称
        dto.setListenerValueTypeCode(dto.getListenerValueTypeCode()); // 监听器值类型
        dto.setListenerValueTypeName(dto.getListenerValueTypeName()); // 监听器值类型名称
        dto.setListenerValue(dto.getListenerValue()); // 监听器值

    }
    /**
     * 校验流程监听器是否存在
     * @param id 监听器ID
     */
    private void validateProcessListenerExists(Long id) {
        if (bpmProcessListenerMapper.getDetail(id) == null) {
            throw new BusinessRuntimeException("流程监听器不存在");
        }
    }

    /**
     * 校验流程监听器的值是否合法
     * @param dto 监听器信息
     */
    private void validateListenerValue(BpmProcessListenerDTO dto) {
        // 校验 CLASS 类型
        if (BpmProcessListenerValueTypeEnum.CLASS.getType().equals(dto.getListenerValueTypeCode())) {
            try {
                Class<?> clazz = Class.forName(dto.getListenerValue());
                // 如果是执行监听器，必须实现 JavaDelegate 接口
                if (BpmProcessListenerTypeEnum.EXECUTION.getType().equals(dto.getListenerTypeCode())
                        && !JavaDelegate.class.isAssignableFrom(clazz)) {
                    throw new BusinessRuntimeException(String.format("流程监听器类(%s)没有实现接口(%s)",
                            dto.getListenerValue(), JavaDelegate.class.getName()));
                }
                // 如果是任务监听器，必须实现 TaskListener 接口
                else if (BpmProcessListenerTypeEnum.TASK.getType().equals(dto.getListenerTypeCode())
                        && !TaskListener.class.isAssignableFrom(clazz)) {
                    throw new BusinessRuntimeException(String.format("流程监听器类(%s)没有实现接口(%s)",
                            dto.getListenerValue(), TaskListener.class.getName()));
                }
            } catch (ClassNotFoundException e) {
                throw new BusinessRuntimeException(String.format("流程监听器类(%s)不存在", dto.getListenerValue()));
            }
            return;
        }

        // 校验 EXPRESSION 和 DELEGATE_EXPRESSION 类型
        // 源码只校验了普通表达式，代理表达式也应遵循此格式
        if (BpmProcessListenerValueTypeEnum.EXPRESSION.getType().equals(dto.getListenerValueTypeCode())
                || BpmProcessListenerValueTypeEnum.DELEGATE_EXPRESSION.getType().equals(dto.getListenerValueTypeCode())) {
            if (!StringUtils.startsWith(dto.getListenerValue(), "${") || !StringUtils.endsWith(dto.getListenerValue(), "}")) {
                throw new BusinessRuntimeException(String.format("流程监听器表达式(%s)不合法，必须以 ${ 开头，以 } 结尾", dto.getListenerValue()));
            }
        }
    }


    /**获取全部使用该监听的流程模型*/
    @Override
    public List<BpmModelDTO> getListenerModel(Long id) {
        BpmProcessListenerDTO dto = bpmProcessListenerMapper.getDetail(id);

        //查询全部的流程模型
        List<Model> list = modelService.getModelList(null);
        if (CollUtil.isEmpty(list)) {
            return null;
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
        List<BpmModelDTO> modelDTOList = BpmModelConvert.INSTANCE.buildModelList(list,
                formMap, categoryMap, deploymentMap, processDefinitionMap,userMap,deptMap);

        List<BpmModelDTO> resultModelList = new ArrayList<>();
        for (BpmModelDTO bpmModelDTO : modelDTOList) {
            byte[] bpmnBytes = modelService.getModelBpmnXML(bpmModelDTO.getId());
            String xmlStr = BpmnModelUtils.getBpmnXml(bpmnBytes);
            if(checkTaskListener(xmlStr,dto.getListenerValue(),dto.getListenerEventCode())){
                resultModelList.add(bpmModelDTO);
            }
        }
        return resultModelList;
    }

    /**
     * 核心方法：检查XML字符串中是否包含指定的taskListener
     * @param xmlStr BPMN XML片段字符串
     * @param targetClass 目标class属性值
     * @param targetEvent 目标event属性值
     * @return 匹配返回true，否则返回false
     */
    public static boolean checkTaskListener(String xmlStr, String targetClass, String targetEvent) {

        // 初始化DOM解析器
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // 开启命名空间支持（必须开启，否则无法识别flowable:前缀的标签）
        factory.setNamespaceAware(true);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            // 3. 解析XML字符串为Document对象
            Document document = builder.parse(new InputSource(new StringReader(xmlStr)));

            // 4. 查找所有flowable:taskListener节点（通过命名空间+标签名）
            NodeList taskListenerNodes = document.getElementsByTagNameNS("http://flowable.org/bpmn", "taskListener");

            // 5. 遍历节点，检查属性是否匹配
            for (int i = 0; i < taskListenerNodes.getLength(); i++) {
                Node node = taskListenerNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element listenerElement = (Element) node;

                    // 获取class和event属性值（属性无命名空间，直接通过属性名获取）
                    String classValue = listenerElement.getAttribute("class");
                    String eventValue = listenerElement.getAttribute("event");

                    // 检查属性值是否完全匹配（注意大小写、空格、特殊字符需一致）
                    if (targetClass.equals(classValue) && targetEvent.equals(eventValue)) {
                        return true;
                    }
                }
            }

            // 未找到匹配的节点
            return false;

        } catch (ParserConfigurationException e) {
            System.err.println("XML解析器配置异常：" + e.getMessage());
            return false;
        } catch (SAXException e) {
            System.err.println("XML语法解析异常：" + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("XML读取IO异常：" + e.getMessage());
            return false;
        }
    }
}
