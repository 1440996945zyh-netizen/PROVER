package com.yy.ppm.flowable.controller;

import cn.hutool.core.collection.CollUtil;
import com.yy.common.enums.Response;
import com.yy.common.flowable.utils.BeanUtils;
import com.yy.common.flowable.utils.DateUtils;
import com.yy.common.flowable.utils.FlowableUtils;
import com.yy.common.flowable.utils.MapUtils;
import com.yy.common.page.Pages;
import com.yy.common.util.PageConverterUtils;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceCopyDTO;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceCopySearchDTO;
import com.yy.ppm.flowable.bean.dto.UserSimpleBaseDTO;
import com.yy.ppm.flowable.bean.po.BpmProcessDefinitionInfoPO;
import com.yy.ppm.flowable.bean.po.BpmProcessInstanceCopyPO;
import com.yy.ppm.flowable.service.BpmProcessDefinitionService;
import com.yy.ppm.flowable.service.BpmProcessInstanceCopyService;
import com.yy.ppm.flowable.service.BpmProcessInstanceService;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.yy.common.flowable.utils.CollectionUtils.convertListByFlatMap;
import static com.yy.common.flowable.utils.CollectionUtils.convertSet;
import static com.yy.common.util.SecurityUtils.getLoginUserId;

/**
 * 流程实例抄送
 */
@RestController
@RequestMapping("/bpm/process-instance/copy")
public class BpmProcessInstanceCopyController {

    @Resource
    private BpmProcessInstanceCopyService processInstanceCopyService;
    @Resource
    private BpmProcessInstanceService processInstanceService;
    @Resource
    private BpmProcessDefinitionService processDefinitionService;

    @Resource
    private SysUserService sysUserService;

    @GetMapping("/page")
    @Operation(summary = "获得抄送流程分页列表")
    @PreAuthorize("@ss.hasPermission('bpm:process-instance-cc:query')")
    public Map<String,Object> getProcessInstanceCopyPage(BpmProcessInstanceCopySearchDTO pageReqVO) {
        pageReqVO.setUserId(getLoginUserId());
        Pages<BpmProcessInstanceCopyPO> pageResult = processInstanceCopyService.getProcessInstanceCopyPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getPages())) {
            return Response.SUCCESS.newBuilder().toResult(pageResult);
        }

        // 拼接返回
        Map<String, HistoricProcessInstance> processInstanceMap = processInstanceService.getHistoricProcessInstanceMap(
                convertSet(pageResult.getPages(), BpmProcessInstanceCopyPO::getProcessInstanceId));
        Map<Long, SysUserDTO> userMap = sysUserService.getUserMap(convertListByFlatMap(pageResult.getPages(),
                copy -> Stream.of(copy.getStartUserId(), copy.getCreateBy())));
        Map<String, BpmProcessDefinitionInfoPO> processDefinitionInfoMap = processDefinitionService.getProcessDefinitionInfoMap(
                convertSet(pageResult.getPages(), BpmProcessInstanceCopyPO::getProcessDefinitionId));

        // 转换分页对象
        Pages<BpmProcessInstanceCopyDTO> dtoPageResult = new Pages<>();
        dtoPageResult.setPageNum(pageResult.getPageNum());
        dtoPageResult.setPageSize(pageResult.getPageSize());
        dtoPageResult.setTotalNum(pageResult.getTotalNum());
        dtoPageResult.setTotalPageNum(pageResult.getTotalPageNum());


        // 2. 手动遍历PO列表，逐个转换为DTO（与原Lambda逻辑完全一致）
        List<BpmProcessInstanceCopyDTO> dtoList = new ArrayList<>();
        for (BpmProcessInstanceCopyPO copy : pageResult.getPages()) {
            BpmProcessInstanceCopyDTO copyVO = BeanUtils.toBean(copy, BpmProcessInstanceCopyDTO.class);
            // 补充启动人信息
            MapUtils.findAndThen(userMap, Long.valueOf(copy.getCreateBy()),
                    user -> copyVO.setStartUser(BeanUtils.toBean(user, UserSimpleBaseDTO.class)));
            // 补充创建人信息
            MapUtils.findAndThen(userMap, copy.getStartUserId(),
                    user -> copyVO.setCreateUser(BeanUtils.toBean(user, UserSimpleBaseDTO.class)));
            // 补充流程摘要和启动时间
            MapUtils.findAndThen(processInstanceMap, copyVO.getProcessInstanceId(),
                    processInstance -> {
                        copyVO.setSummary(FlowableUtils.getSummary(
                                processDefinitionInfoMap.get(processInstance.getProcessDefinitionId()),
                                processInstance.getProcessVariables()));
                        copyVO.setProcessInstanceStartTime(processInstance.getStartTime());
                    });
            dtoList.add(copyVO);
        }
        dtoPageResult.setPages(dtoList);
        return Response.SUCCESS.newBuilder().toResult(dtoPageResult);
    }

}
