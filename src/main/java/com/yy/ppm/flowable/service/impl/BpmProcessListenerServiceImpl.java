package com.yy.ppm.flowable.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.flowable.bean.dto.BpmProcessListenerDTO;
import com.yy.ppm.flowable.bean.dto.BpmProcessListenerSearchDTO;
import com.yy.ppm.flowable.mapper.BpmProcessListenerMapper;
import com.yy.ppm.flowable.service.BpmProcessListenerService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yy.common.flowable.enums.BpmProcessListenerTypeEnum;
import com.yy.common.flowable.enums.BpmProcessListenerValueTypeEnum;
import org.flowable.engine.delegate.TaskListener;

import java.util.Date;

@Service
public class BpmProcessListenerServiceImpl implements BpmProcessListenerService {

    private static final MicroLogger LOGGER = new MicroLogger(BpmProcessListenerServiceImpl.class);

    @Autowired
    private Snowflake snowflake;

    @Resource
    private BpmProcessListenerMapper bpmProcessListenerMapper;

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
}
