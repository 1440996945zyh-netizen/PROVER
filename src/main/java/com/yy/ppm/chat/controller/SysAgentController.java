package com.yy.ppm.chat.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.chat.bean.dto.AgentSearchDTO;
import com.yy.ppm.chat.bean.dto.SysAgentManageDTO;
import com.yy.ppm.chat.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 智能体维护 Controller
 *
 * @author system
 */
@RestController
@RequestMapping(value = "/api/internal/sysAgent")
public class SysAgentController {

    private static final MicroLogger LOGGER = new MicroLogger(SysAgentController.class);

    @Autowired
    private AgentService agentService;

    /**
     * 分页查询智能体列表
     *
     * @param search 查询条件
     * @return 分页结果
     */
    @GetMapping("/getlist")
    @PreAuthorize("hasAuthority('system:agent:query')")
    @Log(title = "查询智能体列表", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getList(AgentSearchDTO search) {
        final String methodName = "SysAgentController:getList";
        LOGGER.enter(methodName + "[start]", "search:" + search);

        Pages<SysAgentManageDTO> pages = agentService.getList(search);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 根据ID获取智能体详情
     *
     * @param id 主键
     * @return 智能体详情
     */
    @GetMapping("/getbyid/{id}")
    @PreAuthorize("hasAuthority('system:agent:query')")
    @Log(title = "根据ID查询智能体", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getById(@PathVariable("id") Long id) {
        final String methodName = "SysAgentController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        SysAgentManageDTO dto = agentService.getByIdForManage(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(dto);
    }

    /**
     * 新增智能体
     *
     * @param dto 智能体信息
     * @return 影响行数
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('system:agent:add')")
    @Log(title = "新增智能体", value = OperateTypeEnum.INSERT)
    public Map<String, Object> insert(@RequestBody SysAgentManageDTO dto) {
        final String methodName = "SysAgentController:insert";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        int count = agentService.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out(count > 0 ? "新增成功" : "新增失败").toResult(count);
    }

    /**
     * 修改智能体
     *
     * @param dto 智能体信息
     * @return 影响行数
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('system:agent:update')")
    @Log(title = "修改智能体", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> update(@RequestBody SysAgentManageDTO dto) {
        final String methodName = "SysAgentController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        int count = agentService.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out(count > 0 ? "修改成功" : "修改失败").toResult(count);
    }

    /**
     * 删除智能体
     *
     * @param idList 主键列表
     * @return 影响行数
     */
    @DeleteMapping("/deletebyid/{idList}")
    @PreAuthorize("hasAuthority('system:agent:delete')")
    @Log(title = "删除智能体", value = OperateTypeEnum.DELETE)
    public Map<String, Object> deleteById(@PathVariable("idList") List<Long> idList) {
        final String methodName = "SysAgentController:deleteById";
        LOGGER.enter(methodName + "[start]", "idList:" + idList);

        int count = agentService.deleteById(idList);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out(count > 0 ? "删除成功" : "删除失败").toResult(count);
    }
}
