package com.yy.ppm.flowable.service.impl;


import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.flowable.bean.dto.BpmBusinessConfigDTO;
import com.yy.ppm.flowable.bean.dto.BpmBusinessConfigSearchDTO;
import com.yy.ppm.flowable.bean.po.BpmProcessDefinitionInfoPO;
import com.yy.ppm.flowable.mapper.BpmBusinessConfigMapper;
import com.yy.ppm.flowable.service.BpmBusinessConfigService;
import com.yy.ppm.flowable.service.BpmProcessDefinitionService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description BPM业务配置Service实现类
 */
@Service
public class BpmBusinessConfigServiceImpl implements BpmBusinessConfigService {

    private static final MicroLogger LOGGER = new MicroLogger(BpmBusinessConfigServiceImpl.class);

    private final Snowflake snowflake;
    public BpmBusinessConfigServiceImpl(Snowflake snowflake) {
        this.snowflake = snowflake;
    }

    @Resource
    private BpmBusinessConfigMapper bpmBusinessConfigMapper;

    @Resource
    private BpmProcessDefinitionService processDefinitionService;

    /**
     * 分页查询列表
     */
    @Override
    public Pages<BpmBusinessConfigDTO> getList(BpmBusinessConfigSearchDTO searchDTO) {
        final String methodName = "BpmBusinessConfigServiceImpl:getList";
        LOGGER.enter(methodName, "分页查询业务配置列表");

        Pages<BpmBusinessConfigDTO> pages = PageHelperUtils.limit(searchDTO,
                () -> bpmBusinessConfigMapper.getList(searchDTO));

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return pages;
    }

    /**
     * 新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(BpmBusinessConfigDTO dto) {
        final String methodName = "BpmBusinessConfigServiceImpl:insert";
        LOGGER.enter(methodName, "新增业务配置");

        // 校验业务按钮是否绑定了其他流程模型
        int count = bpmBusinessConfigMapper.checkUniqueConfig(
                dto.getBusinessId(),
                dto.getBusinessTypeCode()
        );
        if (count > 0) {
            // 抛出自定义异常或业务异常，让前端捕获错误信息
            throw new BusinessRuntimeException("该业务按钮已关联流程，如需修改请进行编辑！");
        }

        // 设置ID
        dto.setId(snowflake.nextId());
        // 获取流程模型对应的最新流程定义ID
        String procDefId = bpmBusinessConfigMapper.getprocDefId(dto.getProcModelId());
        if (StringUtils.isEmpty(procDefId)) {
            // 抛出自定义异常或业务异常，让前端捕获错误信息
            throw new BusinessRuntimeException("流程模型未发布~");
        }
        dto.setProcDefId(procDefId);

        // 设置默认状态
        if (StringUtils.isBlank(dto.getStatus())) {
            dto.setStatus("1");
        }

        bpmBusinessConfigMapper.insert(dto);

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }


    /**
     * 修改
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BpmBusinessConfigDTO dto) {
        final String methodName = "BpmBusinessConfigServiceImpl:update";
        LOGGER.enter(methodName, "修改业务配置");

        if (dto.getId() == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }
        // 1. 唯一性校验：判断该业务模块下的该按钮是否已经配置过流程（排除当前记录ID）
        int count = bpmBusinessConfigMapper.checkUniqueConfigForUpdate(
                dto.getBusinessId(),
                dto.getBusinessTypeCode(),
                dto.getId()
        );

        if (count > 0) {
            throw new BusinessRuntimeException("该业务按钮已关联其他流程配置，请检查！");
        }
        // 获取流程模型对应的最新流程定义ID
        String procDefId = bpmBusinessConfigMapper.getprocDefId(dto.getProcModelId());
        if (StringUtils.isEmpty(procDefId)) {
            // 抛出自定义异常或业务异常，让前端捕获错误信息
            throw new BusinessRuntimeException("流程模型未发布~");
        }
        dto.setProcDefId(procDefId);


        bpmBusinessConfigMapper.update(dto);

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    /**
     * 根据ID删除
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        final String methodName = "BpmBusinessConfigServiceImpl:deleteById";
        LOGGER.enter(methodName, "删除业务配置");

        if (id == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }

        Integer count = bpmBusinessConfigMapper.deleteById(id);
        if (count <= 0) {
            throw new BusinessRuntimeException("删除失败，记录不存在");
        }

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    /**
     * 根据ID查询详情
     */
    @Override
    public BpmBusinessConfigDTO getDetail(Long id) {
        final String methodName = "BpmBusinessConfigServiceImpl:getDetail";
        LOGGER.enter(methodName, "查询业务配置详情");

        if (id == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }

        BpmBusinessConfigDTO dto = bpmBusinessConfigMapper.getDetail(id);
        if (dto == null) {
            throw new BusinessRuntimeException("业务配置不存在");
        }

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return dto;
    }

    /**
     * 根据菜单和流程业务类型获取流程定义
     */
    @Override
    public BpmProcessDefinitionInfoPO getProcDefInfo(Long businessId, String businessTypeCode) {
        // 业务关联中查询流程定义
        String procDefId = bpmBusinessConfigMapper.getProcDefId(businessId, businessTypeCode);
        if (StringUtils.isEmpty(procDefId)) {
            throw new BusinessRuntimeException("业务未关联有效流程定义");
        }
        // 查询表单字段信息并提取
        BpmProcessDefinitionInfoPO processDefinitionInfo = processDefinitionService
                .getProcessDefinitionInfo(procDefId);

        if (processDefinitionInfo == null) {
            throw new BusinessRuntimeException("流程定义的表单信息不存在");
        }

        processDefinitionInfo.setFieldList(processDefinitionInfo.getFieldKeys());

        return processDefinitionInfo;
    }
}
